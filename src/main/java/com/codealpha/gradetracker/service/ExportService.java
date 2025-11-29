package com.codealpha.gradetracker.service;

import com.codealpha.gradetracker.model.Grade;
import com.codealpha.gradetracker.model.Student;
import com.codealpha.gradetracker.repository.GradeRepository;
import com.codealpha.gradetracker.repository.StudentRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExportService {

    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Transactional(readOnly = true)
    public String exportStudentsToCSV() throws IOException {
        List<Student> students = studentRepository.findAll();
        StringWriter sw = new StringWriter();

        try (CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT
                .withHeader("ID", "First Name", "Last Name", "Email", "Phone", "Enrollment ID", "Active", "Created At"))) {
            
            for (Student student : students) {
                printer.printRecord(
                        student.getId(),
                        student.getFirstName(),
                        student.getLastName(),
                        student.getEmail(),
                        student.getPhoneNumber(),
                        student.getEnrollmentId(),
                        student.getActive(),
                        student.getCreatedAt() != null ? student.getCreatedAt().format(FORMATTER) : ""
                );
            }
        }

        log.info("Exported {} students to CSV", students.size());
        return sw.toString();
    }

    @Transactional(readOnly = true)
    public String exportGradesToCSV() throws IOException {
        List<Grade> grades = gradeRepository.findAll();
        StringWriter sw = new StringWriter();

        try (CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT
                .withHeader("ID", "Student Name", "Student Email", "Course Code", "Course Name", 
                           "Score", "Letter Grade", "Assessment", "Date", "Status"))) {
            
            for (Grade grade : grades) {
                printer.printRecord(
                        grade.getId(),
                        grade.getStudent().getFullName(),
                        grade.getStudent().getEmail(),
                        grade.getCourse().getCode(),
                        grade.getCourse().getName(),
                        grade.getNumericScore(),
                        grade.getLetterGrade(),
                        grade.getAssessment(),
                        grade.getGradeDate(),
                        grade.getGradeStatus()
                );
            }
        }

        log.info("Exported {} grades to CSV", grades.size());
        return sw.toString();
    }

    @Transactional(readOnly = true)
    public String exportStudentGradesToCSV(String studentId) throws IOException {
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        if (grades.isEmpty()) {
            throw new IllegalArgumentException("No grades found for student ID: " + studentId);
        }

        StringWriter sw = new StringWriter();
        Student student = grades.get(0).getStudent();

        try (CSVPrinter printer = new CSVPrinter(sw, CSVFormat.DEFAULT
                .withHeader("Student: " + student.getFullName() + " (" + student.getEmail() + ")")
                .withSkipHeaderRecord(false))) {
            
            printer.printRecord(); // Empty line
            printer.printRecord("Course Code", "Course Name", "Score", "Letter Grade", "Assessment", "Date", "Status");

            for (Grade grade : grades) {
                printer.printRecord(
                        grade.getCourse().getCode(),
                        grade.getCourse().getName(),
                        grade.getNumericScore(),
                        grade.getLetterGrade(),
                        grade.getAssessment(),
                        grade.getGradeDate(),
                        grade.getGradeStatus()
                );
            }

            // Add statistics
            Double average = grades.stream()
                    .mapToDouble(Grade::getNumericScore)
                    .average()
                    .orElse(0.0);

            printer.printRecord();
            printer.printRecord("Total Grades", grades.size());
            printer.printRecord("Average Score", String.format("%.2f", average));
        }

        log.info("Exported grades for student: {}", student.getEmail());
        return sw.toString();
    }

    @Transactional(readOnly = true)
    public byte[] exportGradesToPDF() throws IOException {
        List<Grade> grades = gradeRepository.findAll();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Title
            Paragraph title = new Paragraph("Grade Tracker Report")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // Metadata
            Paragraph metadata = new Paragraph("Generated: " + LocalDateTime.now().format(FORMATTER))
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(metadata);

            document.add(new Paragraph("\n"));

            // Table
            float[] columnWidths = {1, 3, 3, 2, 2, 2, 2};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // Headers
            table.addHeaderCell("ID");
            table.addHeaderCell("Student");
            table.addHeaderCell("Course");
            table.addHeaderCell("Score");
            table.addHeaderCell("Grade");
            table.addHeaderCell("Date");
            table.addHeaderCell("Status");

            // Data
            for (Grade grade : grades) {
                table.addCell(grade.getId().toString());
                table.addCell(grade.getStudent().getFullName());
                table.addCell(grade.getCourse().getName());
                table.addCell(grade.getNumericScore().toString());
                table.addCell(grade.getLetterGrade());
                table.addCell(grade.getGradeDate().toString());
                table.addCell(grade.getGradeStatus());
            }

            document.add(table);

            // Footer
            document.add(new Paragraph("\n"));
            Paragraph footer = new Paragraph("Total Records: " + grades.size())
                    .setFontSize(10)
                    .setTextAlignment(TextAlignment.RIGHT);
            document.add(footer);
        }

        log.info("Exported {} grades to PDF", grades.size());
        return baos.toByteArray();
    }

    @Transactional(readOnly = true)
    public byte[] exportStudentGradesToPDF(String studentId) throws IOException {
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        if (grades.isEmpty()) {
            throw new IllegalArgumentException("No grades found for student ID: " + studentId);
        }

        Student student = grades.get(0).getStudent();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {

            // Title
            Paragraph title = new Paragraph("Student Grade Report")
                    .setFontSize(20)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER);
            document.add(title);

            // Student Info
            document.add(new Paragraph("\n"));
            document.add(new Paragraph("Student: " + student.getFullName()).setBold());
            document.add(new Paragraph("Email: " + student.getEmail()));
            document.add(new Paragraph("Enrollment ID: " + student.getEnrollmentId()));
            document.add(new Paragraph("Report Generated: " + LocalDateTime.now().format(FORMATTER)));
            document.add(new Paragraph("\n"));

            // Table
            float[] columnWidths = {3, 3, 2, 2, 2, 2};
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            // Headers
            table.addHeaderCell("Course Code");
            table.addHeaderCell("Course Name");
            table.addHeaderCell("Score");
            table.addHeaderCell("Grade");
            table.addHeaderCell("Assessment");
            table.addHeaderCell("Date");

            // Data
            for (Grade grade : grades) {
                table.addCell(grade.getCourse().getCode());
                table.addCell(grade.getCourse().getName());
                table.addCell(grade.getNumericScore().toString());
                table.addCell(grade.getLetterGrade());
                table.addCell(grade.getAssessment() != null ? grade.getAssessment() : "N/A");
                table.addCell(grade.getGradeDate().toString());
            }

            document.add(table);

            // Statistics
            document.add(new Paragraph("\n"));
            Double average = grades.stream().mapToDouble(Grade::getNumericScore).average().orElse(0.0);
            Double highest = grades.stream().mapToDouble(Grade::getNumericScore).max().orElse(0.0);
            Double lowest = grades.stream().mapToDouble(Grade::getNumericScore).min().orElse(0.0);

            document.add(new Paragraph("Statistics:").setBold());
            document.add(new Paragraph("Total Grades: " + grades.size()));
            document.add(new Paragraph(String.format("Average Score: %.2f", average)));
            document.add(new Paragraph(String.format("Highest Score: %.2f", highest)));
            document.add(new Paragraph(String.format("Lowest Score: %.2f", lowest)));
        }

        log.info("Exported PDF report for student: {}", student.getEmail());
        return baos.toByteArray();
    }
}
