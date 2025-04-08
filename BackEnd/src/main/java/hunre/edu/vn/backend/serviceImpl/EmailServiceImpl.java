package hunre.edu.vn.backend.serviceImpl;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.barcodes.BarcodeQRCode;

import hunre.edu.vn.backend.dto.MedicineMediaDTO;
import hunre.edu.vn.backend.dto.OrderDTO;
import hunre.edu.vn.backend.dto.OrderDetailDTO;
import hunre.edu.vn.backend.entity.Order;
import hunre.edu.vn.backend.entity.ServiceBooking;
import hunre.edu.vn.backend.entity.User;
import hunre.edu.vn.backend.exception.ResourceNotFoundException;
import hunre.edu.vn.backend.exception.ServiceException;
import hunre.edu.vn.backend.repository.OrderRepository;
import hunre.edu.vn.backend.repository.ServiceBookingRepository;
import hunre.edu.vn.backend.service.EmailService;
import hunre.edu.vn.backend.service.OrderDetailService;
import hunre.edu.vn.backend.service.OrderService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class EmailServiceImpl implements EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceImpl.class);

    // Constants
    private static final String DATE_FORMAT_PATTERN = "dd/MM/yyyy HH:mm";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_PATTERN);
    private static final Locale VIETNAM_LOCALE = new Locale("vi", "VN");
    private static final String COMPANY_NAME = "THAPV Pharmacity";
    private static final String COMPANY_ADDRESS = "Số 285, Đội Cấn, Ba Đình, Hà Nội";
    private static final BigDecimal TAX_RATE = new BigDecimal("0.1"); // 10%
    private static final int PDF_COMPRESSION_LEVEL = 9;
    private static final String COMPANY_LOGO_URL = "https://medicinemedia.s3.ap-southeast-2.amazonaws.com/logo.png";

    // Dependencies
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final ServiceBookingRepository serviceBookingRepository;
    private final OrderService orderService;
    private final OrderDetailService orderDetailService;
    private final OrderRepository orderRepository;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${application.frontend.url}")
    private String frontendUrl;

    public EmailServiceImpl(
            JavaMailSender mailSender,
            TemplateEngine templateEngine,
            ServiceBookingRepository serviceBookingRepository,
            @Lazy OrderService orderService,
            @Lazy OrderDetailService orderDetailService,
            OrderRepository orderRepository) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.serviceBookingRepository = serviceBookingRepository;
        this.orderService = orderService;
        this.orderDetailService = orderDetailService;
        this.orderRepository = orderRepository;
    }

    @Override
    public void sendOrderConfirmationEmail(Long orderId, String userEmail, String userName) throws MessagingException {
        try {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Order not found"));
            Optional<OrderDTO.GetOrderDTO> orderDTOOptional = orderService.findById(order.getId());

            if (orderDTOOptional.isEmpty()) {
                logger.error("Không tìm thấy thông tin đơn hàng với ID: {}", order.getId());
                throw new ResourceNotFoundException("Không tìm thấy thông tin đơn hàng với ID: " + order.getId());
            }

            OrderDTO.GetOrderDTO orderDTO = orderDTOOptional.get();

            // Lấy chi tiết đơn hàng từ service
            List<OrderDetailDTO.GetOrderDetailDTO> orderDetailDTOs = orderDetailService.findByOrderId(orderDTO.getId());


            orderDTO.setOrderDetails(orderDetailDTOs);

            // Kiểm tra template trước khi gửi
            try {
                Context testContext = prepareOrderContext(userName, orderDTO);
                templateEngine.process("order-confirmation-template", testContext);
            } catch (Exception e) {
                logger.error("Lỗi khi kiểm tra template cho đơn hàng {}: {}", orderDTO.getOrderCode(), e.getMessage());
                throw new ServiceException("Template email không khớp với dữ liệu: " + e.getMessage(), e);
            }

            // Chuẩn bị và gửi email
            sendEmailWithAttachment(
                    userEmail,
                    "Xác nhận đơn hàng #" + order.getOrderCode(),
                    "order-confirmation-template",
                    prepareOrderContext(userName, orderDTO),
                    "HoaDon_" + order.getOrderCode() + ".pdf",
                    () -> generateOrderInvoicePdf(orderDTO, userName, userEmail)
            );

            logger.info("Đã gửi email xác nhận đơn hàng #{} cho {}", order.getOrderCode(), userEmail);
        } catch (Exception e) {
            logger.error("Lỗi khi gửi email xác nhận đơn hàng: {}", e.getMessage(), e);
            throw new ServiceException("Không thể gửi email xác nhận đơn hàng: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendServiceBookingConfirmationEmail(User user, ServiceBooking serviceBooking) throws MessagingException {
        try {
            // Tải lại service booking để đảm bảo có đầy đủ thông tin
            ServiceBooking fullServiceBooking = serviceBookingRepository.findById(serviceBooking.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đặt dịch vụ với ID: " + serviceBooking.getId()));

            // Chuẩn bị và gửi email
            sendEmailWithAttachment(
                    user.getEmail(),
                    "Xác nhận đặt dịch vụ",
                    "service-booking-confirmation-template",
                    prepareServiceBookingContext(user, fullServiceBooking),
                    "XacNhanDichVu_" + serviceBooking.getId() + ".pdf",
                    () -> generateServiceBookingPdf(fullServiceBooking)
            );

            logger.info("Đã gửi email xác nhận dịch vụ #{} cho {}", serviceBooking.getId(), user.getEmail());
        } catch (Exception e) {
            logger.error("Lỗi khi gửi email xác nhận dịch vụ: {}", e.getMessage(), e);
            throw new ServiceException("Không thể gửi email xác nhận dịch vụ", e);
        }
    }

    @Override
    public void sendPasswordResetEmail(User user, String resetToken) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Đặt lại mật khẩu");

            Context context = new Context();
            context.setVariable("name", user.getFullName());
            context.setVariable("resetLink", buildResetPasswordUrl(resetToken));

            String emailContent = templateEngine.process("password-reset-template", context);
            helper.setText(emailContent, true);

            mailSender.send(message);

            logger.info("Đã gửi email đặt lại mật khẩu cho {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Lỗi khi tạo email đặt lại mật khẩu: {}", e.getMessage(), e);
            throw new ServiceException("Không thể gửi email đặt lại mật khẩu", e);
        }
    }

    @Override
    public void sendVerificationEmail(User user, String verificationToken) throws MessagingException {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Xác minh tài khoản");

            Context context = new Context();
            context.setVariable("name", user.getFullName());
            context.setVariable("verificationLink", buildVerificationUrl(verificationToken));

            String emailContent = templateEngine.process("account-verification-template", context);
            helper.setText(emailContent, true);

            mailSender.send(message);

            logger.info("Đã gửi email xác minh tài khoản cho {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Lỗi khi tạo email xác minh tài khoản: {}", e.getMessage(), e);
            throw new ServiceException("Không thể gửi email xác minh tài khoản", e);
        }
    }

    /**
     * Gửi email với tập tin đính kèm được tạo bởi pdfGenerator
     */
    private void sendEmailWithAttachment(
            String toEmail,
            String subject,
            String templateName,
            Context context,
            String attachmentFilename,
            PdfGenerator pdfGenerator) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(senderEmail);
        helper.setTo(toEmail);
        helper.setSubject(subject);

        String emailContent = templateEngine.process(templateName, context);
        helper.setText(emailContent, true);

        // Tạo và đính kèm PDF
        try {
            byte[] pdfContent = pdfGenerator.generate();

            // Kiểm tra kích thước PDF
            if (pdfContent == null || pdfContent.length == 0) {
                logger.warn("PDF tạo ra có kích thước 0 byte");
            } else {
                logger.info("Đã tạo PDF thành công với kích thước {} bytes", pdfContent.length);
                helper.addAttachment(
                        attachmentFilename,
                        new ByteArrayResource(pdfContent),
                        "application/pdf"
                );
            }
        } catch (Exception e) {
            logger.error("Lỗi khi tạo PDF đính kèm: {}", e.getMessage(), e);
            throw new ServiceException("Không thể tạo PDF đính kèm", e);
        }

        // Gửi email
        try {
            mailSender.send(message);
        } catch (Exception e) {
            logger.error("Lỗi gửi email: {}", e.getMessage(), e);
            throw new MessagingException("Không thể gửi email", e);
        }
    }

    /**
     * Chuẩn bị Context cho template đơn hàng
     */
    private Context prepareOrderContext(String userName, OrderDTO.GetOrderDTO orderDTO) {
        Context context = new Context();
        context.setVariable("name", userName);
        context.setVariable("orderCode", orderDTO.getOrderCode());
        context.setVariable("orderDate", formatDateTime(orderDTO.getCreatedAt()));
        context.setVariable("totalPrice", formatCurrency(orderDTO.getTotalPrice()));
        context.setVariable("paymentMethod", getPaymentMethodName(orderDTO.getPaymentMethod().toString()));
        context.setVariable("status", getOrderStatusName(orderDTO.getStatus().toString()));

        List<OrderDetailDTO.GetOrderDetailDTO> details = orderDTO.getOrderDetails();

        Map<Long, String> orderDetailMainImages = new HashMap<>();
        for (OrderDetailDTO.GetOrderDetailDTO detailDTO : details) {
            List<MedicineMediaDTO.GetMedicineMediaDTO> medicineMediaDTO = detailDTO.getMedicine().getMedias();
            if (medicineMediaDTO != null && !medicineMediaDTO.isEmpty()) {
                for (MedicineMediaDTO.GetMedicineMediaDTO media : medicineMediaDTO) {
                    if (media.getMainImage()) {
                        orderDetailMainImages.put(detailDTO.getId(), media.getMediaUrl());
                        break;
                    }
                }
            }
        }

        context.setVariable("orderDetailMainImages", orderDetailMainImages);

        context.setVariable("orderDetails", details);
        context.setVariable("discountAmount", orderDTO.getDiscountAmount() != null ?
                formatCurrency(orderDTO.getDiscountAmount()) : "0 VNĐ");
        context.setVariable("note", orderDTO.getNote());
        return context;
    }

    /**
     * Chuẩn bị Context cho template đặt dịch vụ
     */
    private Context prepareServiceBookingContext(User user, ServiceBooking serviceBooking) {
        Context context = new Context();
        context.setVariable("name", user.getFullName());
        context.setVariable("serviceName", serviceBooking.getService().getName());
        context.setVariable("bookingDate", formatDateTime(serviceBooking.getCreatedAt()));
        context.setVariable("appointmentDate", serviceBooking.getAppointment().getAppointmentDate());
        context.setVariable("appointmentTime", serviceBooking.getAppointment().getAppointmentTime());
        context.setVariable("doctorName", serviceBooking.getDoctor().getUser().getFullName());
        context.setVariable("totalPrice", formatCurrency(serviceBooking.getTotalPrice()));
        context.setVariable("status", getServiceBookingStatusName(serviceBooking.getStatus().toString()));
        context.setVariable("paymentMethod", getPaymentMethodName(serviceBooking.getPaymentMethod().toString()));
        return context;
    }

    /**
     * Tạo đối tượng Image từ URL
     */
    private Image createImageFromUrl(String imageUrl, float width, float height) {
        try {
            // Đọc ảnh và chuyển đổi
            BufferedImage originalImage = Thumbnails.of(new URL(imageUrl))
                    .size((int)width, (int)height)
                    .outputFormat("png")
                    .asBufferedImage();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "png", baos);

            ImageData imageData = ImageDataFactory.create(baos.toByteArray());
            return new Image(imageData)
                    .setWidth(width)
                    .setHeight(height);
        } catch (Exception e) {
            logger.warn("Lỗi khi tạo ảnh từ URL {}: {}", imageUrl, e.getMessage());
            return null;
        }
    }

    /**
     * Tìm URL ảnh chính từ danh sách media
     */
    private String findMainImageUrl(List<MedicineMediaDTO.GetMedicineMediaDTO> medias) {
        if (medias == null || medias.isEmpty()) {
            return null;
        }

        // Ưu tiên ảnh chính
        Optional<MedicineMediaDTO.GetMedicineMediaDTO> mainImage = medias.stream()
                .filter(media -> Boolean.TRUE.equals(media.getMainImage()) &&
                        media.getMediaUrl() != null &&
                        !media.getMediaUrl().trim().isEmpty())
                .findFirst();

        // Nếu không có ảnh chính, lấy ảnh đầu tiên có URL
        if (mainImage.isPresent()) {
            return mainImage.get().getMediaUrl();
        }

        Optional<MedicineMediaDTO.GetMedicineMediaDTO> firstImage = medias.stream()
                .filter(media -> media.getMediaUrl() != null &&
                        !media.getMediaUrl().trim().isEmpty())
                .findFirst();

        return firstImage.map(MedicineMediaDTO.GetMedicineMediaDTO::getMediaUrl).orElse(null);
    }

    /**
     * Tạo PDF hóa đơn đặt hàng
     */
    private byte[] generateOrderInvoicePdf(OrderDTO.GetOrderDTO orderDTO, String userName, String userEmail) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf, PageSize.A4)) {

            // Sử dụng font hỗ trợ tiếng Việt
            PdfFont regularFont = PdfFontFactory.createFont("fonts/Unicode/arial.ttf");
            PdfFont boldFont = PdfFontFactory.createFont("fonts/Unicode/arialbd.ttf");

            // Thêm tiêu đề
            document.add(new Paragraph("HÓA ĐƠN")
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Thông tin đơn hàng
            document.add(new Paragraph("Mã đơn hàng: " + orderDTO.getOrderCode())
                    .setFont(regularFont)
                    .setFontSize(12));
            document.add(new Paragraph("Ngày đặt: " + formatDateTime(orderDTO.getCreatedAt()))
                    .setFont(regularFont)
                    .setFontSize(12));
            document.add(new Paragraph("Khách hàng: " + userName)
                    .setFont(regularFont)
                    .setFontSize(12));

            // Chi tiết đơn hàng
            List<OrderDetailDTO.GetOrderDetailDTO> details = orderDTO.getOrderDetails();
            if (details != null && !details.isEmpty()) {
                // Tạo bảng chi tiết
                float[] columnWidths = {2, 1, 1, 1};
                Table table = new Table(columnWidths).useAllAvailableWidth();

                // Tiêu đề cột
                String[] headers = {"Sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"};
                for (String header : headers) {
                    table.addHeaderCell(new Cell()
                            .add(new Paragraph(header).setFont(boldFont))
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY));
                }

                // Dữ liệu sản phẩm
                BigDecimal totalAmount = BigDecimal.ZERO;
                for (OrderDetailDTO.GetOrderDetailDTO detail : details) {
                    BigDecimal lineTotal = detail.getUnitPrice()
                            .multiply(BigDecimal.valueOf(detail.getQuantity()));
                    totalAmount = totalAmount.add(lineTotal);

                    table.addCell(new Cell().add(new Paragraph(detail.getMedicine().getName()).setFont(regularFont)));
                    table.addCell(new Cell().add(new Paragraph(String.valueOf(detail.getQuantity())).setFont(regularFont)));
                    table.addCell(new Cell().add(new Paragraph(formatCurrency(detail.getUnitPrice())).setFont(regularFont)));
                    table.addCell(new Cell().add(new Paragraph(formatCurrency(lineTotal)).setFont(regularFont)));
                }

                document.add(table);

                // Tổng tiền
                document.add(new Paragraph("Tổng cộng: " + formatCurrency(totalAmount))
                        .setFont(boldFont)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setFontSize(14)
                        .setMarginTop(10));
            } else {
                document.add(new Paragraph("Không có sản phẩm trong đơn hàng")
                        .setFont(regularFont)
                        .setTextAlignment(TextAlignment.CENTER));
            }

            // Thêm QR code
            addQRCode(document, orderDTO.getOrderCode());

            document.close();

            byte[] pdfBytes = baos.toByteArray();
            logger.info("Tạo PDF thành công - Mã đơn hàng: {}, Kích thước: {} bytes",
                    orderDTO.getOrderCode(), pdfBytes.length);

            return pdfBytes;
        } catch (Exception e) {
            logger.error("Lỗi khi tạo PDF cho đơn hàng {}: {}",
                    orderDTO.getOrderCode(), e.getMessage(), e);
            throw new ServiceException("Không thể tạo PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Tạo PDF xác nhận đặt dịch vụ
     */
    /**
     * Tạo PDF xác nhận đặt dịch vụ
     */
    private byte[] generateServiceBookingPdf(ServiceBooking serviceBooking) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (PdfWriter writer = new PdfWriter(baos);
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf, PageSize.A4)) {

            logger.info("Bắt đầu tạo PDF cho đặt dịch vụ ID: {}", serviceBooking.getId());

            // Sử dụng font hỗ trợ tiếng Việt hoặc font mặc định nếu không tìm thấy
            PdfFont regularFont, boldFont;
            try {
                regularFont = PdfFontFactory.createFont("fonts/Unicode/arial.ttf");
                boldFont = PdfFontFactory.createFont("fonts/Unicode/arialbd.ttf");
                logger.debug("Đã tải font tùy chỉnh thành công");
            } catch (IOException e) {
                logger.warn("Không thể tải font tùy chỉnh, sử dụng font mặc định: {}", e.getMessage());
                regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
                boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            }

            // Thêm logo nếu có
            try {
                if (COMPANY_LOGO_URL != null && !COMPANY_LOGO_URL.isEmpty()) {
                    ImageData imageData = ImageDataFactory.create(new URL(COMPANY_LOGO_URL));
                    Image logo = new Image(imageData).setWidth(50).setHeight(50);
                    document.add(logo);
                    logger.debug("Đã thêm logo công ty");
                }
            } catch (Exception e) {
                logger.warn("Không thể thêm logo công ty: {}", e.getMessage());
            }

            // Thêm thông tin công ty
            document.add(new Paragraph(COMPANY_NAME)
                    .setFont(boldFont)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(12));
            document.add(new Paragraph(COMPANY_ADDRESS)
                    .setFont(regularFont)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(10)
                    .setMarginBottom(10));

            // Thêm tiêu đề
            document.add(new Paragraph("XÁC NHẬN DỊCH VỤ")
                    .setFont(boldFont)
                    .setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20));

            // Thông tin khách hàng
            User user = serviceBooking.getPatient().getUser();
            document.add(new Paragraph("Khách hàng: " + user.getFullName())
                    .setFont(regularFont)
                    .setFontSize(12));
            document.add(new Paragraph("Email: " + user.getEmail())
                    .setFont(regularFont)
                    .setFontSize(12)
                    .setMarginBottom(10));

            // Thông tin dịch vụ
            document.add(new Paragraph("Mã đặt dịch vụ: " + serviceBooking.getId())
                    .setFont(regularFont)
                    .setFontSize(12));
            document.add(new Paragraph("Dịch vụ: " +
                    (serviceBooking.getService() != null ? serviceBooking.getService().getName() : "N/A"))
                    .setFont(regularFont)
                    .setFontSize(12));
            document.add(new Paragraph("Bác sĩ: " +
                    (serviceBooking.getDoctor() != null && serviceBooking.getDoctor().getUser() != null ?
                            serviceBooking.getDoctor().getUser().getFullName() : "N/A"))
                    .setFont(regularFont)
                    .setFontSize(12));
            document.add(new Paragraph("Ngày đặt: " + formatDateTime(serviceBooking.getCreatedAt()))
                    .setFont(regularFont)
                    .setFontSize(12));

            if (serviceBooking.getAppointment() != null) {
                document.add(new Paragraph("Ngày hẹn: " + serviceBooking.getAppointment().getAppointmentDate())
                        .setFont(regularFont)
                        .setFontSize(12));
                document.add(new Paragraph("Giờ hẹn: " + serviceBooking.getAppointment().getAppointmentTime())
                        .setFont(regularFont)
                        .setFontSize(12));
            }

            document.add(new Paragraph("Phương thức thanh toán: " +
                    getPaymentMethodName(serviceBooking.getPaymentMethod().toString()))
                    .setFont(regularFont)
                    .setFontSize(12));
            document.add(new Paragraph("Trạng thái: " +
                    getServiceBookingStatusName(serviceBooking.getStatus().toString()))
                    .setFont(regularFont)
                    .setFontSize(12)
                    .setMarginBottom(20));

            // Tạo bảng chi tiết dịch vụ
            float[] columnWidths = {3, 1, 2};
            Table table = new Table(columnWidths).useAllAvailableWidth();

            // Tiêu đề cột
            String[] headers = {"Dịch vụ", "Số lượng", "Thành tiền"};
            for (String header : headers) {
                table.addHeaderCell(new Cell()
                        .add(new Paragraph(header).setFont(boldFont))
                        .setBackgroundColor(ColorConstants.LIGHT_GRAY));
            }

            // Dữ liệu dịch vụ
            String serviceName = serviceBooking.getService() != null ?
                    serviceBooking.getService().getName() : "Dịch vụ không xác định";

            table.addCell(new Cell().add(new Paragraph(serviceName).setFont(regularFont)));
            table.addCell(new Cell().add(new Paragraph("1").setFont(regularFont)));
            table.addCell(new Cell().add(new Paragraph(formatCurrency(serviceBooking.getTotalPrice())).setFont(regularFont)));

            document.add(table);

            // Tổng tiền
            document.add(new Paragraph("Tổng cộng: " + formatCurrency(serviceBooking.getTotalPrice()))
                    .setFont(boldFont)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(14)
                    .setMarginTop(10)
                    .setMarginBottom(20));

            // Thêm ghi chú thanh toán
            document.add(new Paragraph("Ghi chú:")
                    .setFont(boldFont)
                    .setFontSize(12));
            document.add(new Paragraph("- Vui lòng đến đúng giờ hẹn")
                    .setFont(regularFont)
                    .setFontSize(10));
            document.add(new Paragraph("- Mang theo giấy tờ tùy thân khi đến khám")
                    .setFont(regularFont)
                    .setFontSize(10)
                    .setMarginBottom(20));

            // Thêm chân trang
            document.add(new Paragraph(COMPANY_NAME)
                    .setFont(boldFont)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(10));
            document.add(new Paragraph("Cảm ơn quý khách đã sử dụng dịch vụ!")
                    .setFont(regularFont)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(8));

            // Thêm QR code
            try {
                BarcodeQRCode qrCode = new BarcodeQRCode("SERVICE:" + serviceBooking.getId().toString());
                PdfFormXObject qrCodeObject = qrCode.createFormXObject(ColorConstants.BLACK, document.getPdfDocument());
                Image qrCodeImage = new Image(qrCodeObject).setWidth(60).setHeight(60);

                // Thêm vào góc phải trên của tài liệu
                qrCodeImage.setFixedPosition(
                        document.getPdfDocument().getDefaultPageSize().getWidth() - 80,
                        document.getPdfDocument().getDefaultPageSize().getHeight() - 80
                );

                document.add(qrCodeImage);
                logger.debug("Đã thêm mã QR thành công");
            } catch (Exception e) {
                logger.warn("Không thể tạo mã QR: {}", e.getMessage());
            }

            // Tự động đóng document thông qua try-with-resources

            byte[] pdfBytes = baos.toByteArray();
            logger.info("Tạo PDF thành công cho đặt dịch vụ ID: {}, Kích thước: {} bytes",
                    serviceBooking.getId(), pdfBytes.length);

            return pdfBytes;
        } catch (Exception e) {
            logger.error("Lỗi khi tạo PDF cho đặt dịch vụ {}: {}",
                    serviceBooking.getId(), e.getMessage(), e);
            throw new ServiceException("Không thể tạo PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Thêm mã QR vào góc phải trên của PDF
     */
    private void addQRCode(Document document, String code) {
        try {
            // Tạo nội dung cho mã QR
            String qrContent = code;

            // Tạo mã QR bằng BarcodeQRCode
            BarcodeQRCode qrCode = new BarcodeQRCode(qrContent);
            PdfFormXObject qrCodeObject = qrCode.createFormXObject(ColorConstants.BLACK, document.getPdfDocument());
            Image qrCodeImage = new Image(qrCodeObject).setWidth(60).setHeight(60);

            // Thêm vào góc phải trên của tài liệu
            document.add(qrCodeImage.setFixedPosition(
                    document.getPdfDocument().getDefaultPageSize().getWidth() - 80,
                    document.getPdfDocument().getDefaultPageSize().getHeight() - 80
            ));

            logger.debug("Đã thêm mã QR cho {}", code);
        } catch (Exception e) {
            logger.warn("Không thể tạo mã QR: {}", e.getMessage());
        }
    }

    /**
     * Thêm tem "Đã thanh toán" cho đơn hàng đã hoàn thành
     */
    private void addPaymentStamp(Document document, OrderDTO.GetOrderDTO orderDTO) {
        // Kiểm tra trạng thái thanh toán
        boolean isPaid = "COMPLETED".equals(orderDTO.getStatus().toString());

        if (isPaid) {
            try {
                // Tạo tem "Đã thanh toán"
                PdfCanvas canvas = new PdfCanvas(document.getPdfDocument().getLastPage());
                canvas.saveState();

                // Cài đặt màu và độ trong suốt
                canvas.setFillColor(ColorConstants.GREEN);
                canvas.setExtGState(new PdfExtGState().setFillOpacity(0.3f));

                // Vẽ tem đã thanh toán
                Rectangle pageSize = document.getPdfDocument().getLastPage().getPageSize();
                canvas.rectangle(pageSize.getWidth() - 200, pageSize.getHeight() - 300, 150, 80);
                canvas.fill();

                // Thêm text
                canvas.setFillColor(ColorConstants.BLACK);
                canvas.beginText();
                PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
                canvas.setFontAndSize(font, 30);
                canvas.setTextMatrix(pageSize.getWidth() - 190, pageSize.getHeight() - 250);
                canvas.showText("ĐÃ THANH TOÁN");
                canvas.endText();

                canvas.restoreState();

                logger.debug("Đã thêm tem đã thanh toán cho đơn hàng {}", orderDTO.getOrderCode());
            } catch (Exception e) {
                logger.warn("Không thể thêm tem đã thanh toán: {}", e.getMessage());
            }
        }
    }

    /**
     * Thêm watermark vào tất cả các trang của PDF
     */
    private void addWatermark(PdfDocument pdfDocument, String text) {
        try {
            int numberOfPages = pdfDocument.getNumberOfPages();
            PdfFont font = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Tạo watermark trên mỗi trang
            for (int i = 1; i <= numberOfPages; i++) {
                PdfCanvas canvas = new PdfCanvas(pdfDocument.getPage(i));

                // Lưu trạng thái hiện tại
                canvas.saveState();

                // Cài đặt độ trong suốt
                canvas.setExtGState(new PdfExtGState().setFillOpacity(0.2f));

                // Cấu hình văn bản
                Rectangle pageSize = pdfDocument.getPage(i).getPageSize();
                canvas.beginText();
                canvas.setFontAndSize(font, 60);

                // Xoay văn bản
                AffineTransform transform = AffineTransform.getRotateInstance(
                        Math.PI / 4, pageSize.getWidth() / 2, pageSize.getHeight() / 2);
                canvas.setTextMatrix(
                        (float)transform.getScaleX(), (float)transform.getShearY(),
                        (float)transform.getShearX(), (float)transform.getScaleY(),
                        (float)(transform.getTranslateX() + pageSize.getWidth() / 4),
                        (float)(transform.getTranslateY() + pageSize.getHeight() / 4));

                // Thêm text
                canvas.showText(text);
                canvas.endText();

                // Khôi phục trạng thái
                canvas.restoreState();
            }

            logger.debug("Đã thêm watermark '{}'", text);
        } catch (Exception e) {
            logger.warn("Không thể thêm watermark: {}", e.getMessage());
        }
    }

    /**
     * Phương thức hỗ trợ thêm tiêu đề công ty vào tài liệu PDF
     */
    private void addCompanyHeader(Document document, PdfFont boldFont, PdfFont regularFont) {
        // Thêm logo nếu có
        try {
            Image logo = createImageFromUrl(COMPANY_LOGO_URL, 50, 50);
            if (logo != null) {
                document.add(logo);
            }
        } catch (Exception e) {
            logger.warn("Không thể thêm logo: {}", e.getMessage());
        }

        document.add(new Paragraph(COMPANY_NAME)
                .setFont(boldFont)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(12));
        document.add(new Paragraph(COMPANY_ADDRESS)
                .setFont(regularFont)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(10));
    }

    /**
     * Phương thức hỗ trợ thêm thông tin khách hàng và đơn hàng vào tài liệu PDF
     */
    private void addCustomerAndOrderInfo(Document document, String userName, String userEmail, OrderDTO.GetOrderDTO orderDTO, PdfFont regularFont) {
        addCustomerInfo(document, userName, userEmail, regularFont);

        document.add(new Paragraph("Mã đơn hàng: " + orderDTO.getOrderCode())
                .setFont(regularFont)
                .setFontSize(10));
        document.add(new Paragraph("Ngày: " + formatDateTime(orderDTO.getCreatedAt()))
                .setFont(regularFont)
                .setFontSize(10));
    }

    /**
     * Phương thức hỗ trợ thêm thông tin khách hàng vào tài liệu PDF
     */
    private void addCustomerInfo(Document document, String userName, String userEmail, PdfFont regularFont) {
        document.add(new Paragraph("Khách hàng: " + userName)
                .setFont(regularFont)
                .setFontSize(10));
        document.add(new Paragraph("Email: " + userEmail)
                .setFont(regularFont)
                .setFontSize(10));
    }

    /**
     * Phương thức hỗ trợ thêm thông tin dịch vụ vào tài liệu PDF
     */
    private void addServiceInfo(Document document, ServiceBooking serviceBooking, PdfFont regularFont) {
        document.add(new Paragraph("Dịch vụ: " + serviceBooking.getService().getName())
                .setFont(regularFont)
                .setFontSize(10));
        document.add(new Paragraph("Bác sĩ: " + serviceBooking.getDoctor().getUser().getFullName())
                .setFont(regularFont)
                .setFontSize(10));
        document.add(new Paragraph("Ngày đặt: " + formatDateTime(serviceBooking.getCreatedAt()))
                .setFont(regularFont)
                .setFontSize(10));
        document.add(new Paragraph("Ngày hẹn: " +
                serviceBooking.getAppointment().getAppointmentDate() + " " +
                serviceBooking.getAppointment().getAppointmentTime())
                .setFont(regularFont)
                .setFontSize(10));
    }

    /**
     * Phương thức hỗ trợ tạo bảng chi tiết dịch vụ trong tài liệu PDF
     */
    private Table createServiceDetailsTable(ServiceBooking serviceBooking, PdfFont boldFont, PdfFont regularFont) {
        float[] columnWidths = {1, 3, 2, 2};
        Table table = new Table(columnWidths).useAllAvailableWidth();

        // Header bảng
        String[] headers = {"Ảnh", "Dịch vụ", "Giá", "Trạng thái"};
        for (String header : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(header).setFont(boldFont))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setFontSize(10));
        }

        // Ảnh dịch vụ
        Cell imageCell = new Cell();
        if (serviceBooking.getService().getImage() != null) {
            Image serviceImage = createImageFromUrl(serviceBooking.getService().getImage(), 30, 30);
            if (serviceImage != null) {
                imageCell.add(serviceImage);
            } else {
                imageCell.add(new Paragraph("Không có ảnh").setFontSize(8));
            }
        } else {
            imageCell.add(new Paragraph("Không có ảnh").setFontSize(8));
        }
        table.addCell(imageCell);

        // Thông tin dịch vụ
        table.addCell(new Cell()
                .add(new Paragraph(serviceBooking.getService().getName())
                        .setFont(regularFont)
                        .setFontSize(10)));

        table.addCell(new Cell()
                .add(new Paragraph(formatCurrency(serviceBooking.getTotalPrice()))
                        .setFont(regularFont)
                        .setFontSize(10)));

        table.addCell(new Cell()
                .add(new Paragraph(getServiceBookingStatusName(serviceBooking.getStatus().toString()))
                        .setFont(regularFont)
                        .setFontSize(10)));

        return table;
    }
    /**
     * Phương thức hỗ trợ tạo bảng chi tiết đơn hàng trong tài liệu PDF
     */
    private Table createOrderDetailsTable(List<OrderDetailDTO.GetOrderDetailDTO> details, PdfFont boldFont, PdfFont regularFont) {
        float[] columnWidths = {1, 3, 1, 2, 2};
        Table table = new Table(columnWidths).useAllAvailableWidth();

        // Header bảng
        String[] headers = {"Ảnh", "Sản phẩm", "Số lượng", "Đơn giá", "Thành tiền"};
        for (String header : headers) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(header).setFont(boldFont))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setFontSize(10));
        }

        // Dữ liệu sản phẩm
        for (OrderDetailDTO.GetOrderDetailDTO detail : details) {
            BigDecimal lineTotal = detail.getUnitPrice()
                    .multiply(BigDecimal.valueOf(detail.getQuantity()));

            // Ảnh sản phẩm - lấy ảnh chính nếu có
            Cell imageCell = new Cell();
            String imageUrl = null;
            if (detail.getMedicine() != null && detail.getMedicine().getMedias() != null && !detail.getMedicine().getMedias().isEmpty()) {
                imageUrl = findMainImageUrl(detail.getMedicine().getMedias());
            }

            if (imageUrl != null) {
                Image image = createImageFromUrl(imageUrl, 30, 30);
                if (image != null) {
                    imageCell.add(image);
                } else {
                    imageCell.add(new Paragraph("Không có ảnh").setFontSize(8));
                }
            } else {
                imageCell.add(new Paragraph("Không có ảnh").setFontSize(8));
            }
            table.addCell(imageCell);

            // Sản phẩm
            table.addCell(new Cell()
                    .add(new Paragraph(detail.getMedicine().getName())
                            .setFont(regularFont)
                            .setFontSize(10)));

            // Số lượng
            table.addCell(new Cell()
                    .add(new Paragraph(String.valueOf(detail.getQuantity()))
                            .setFont(regularFont)
                            .setFontSize(10)));

            // Đơn giá
            table.addCell(new Cell()
                    .add(new Paragraph(formatCurrency(detail.getUnitPrice()))
                            .setFont(regularFont)
                            .setFontSize(10)));

            // Thành tiền
            table.addCell(new Cell()
                    .add(new Paragraph(formatCurrency(lineTotal))
                            .setFont(regularFont)
                            .setFontSize(10)));
        }

        return table;
    }

    /**
     * Phương thức hỗ trợ thêm phần tổng tiền, thuế và thanh toán vào tài liệu PDF
     */
    private void addTotalsSection(Document document, BigDecimal totalAmount, PdfFont boldFont, PdfFont regularFont) {
        document.add(new Paragraph("Tổng tiền: " + formatCurrency(totalAmount))
                .setFont(boldFont)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(12)
                .setMarginTop(10));

        // Thuế
        BigDecimal tax = totalAmount.multiply(TAX_RATE);
        document.add(new Paragraph("Thuế (10%): " + formatCurrency(tax))
                .setFont(regularFont)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(10));

        // Tổng thanh toán
        BigDecimal totalPayment = totalAmount.add(tax);
        document.add(new Paragraph("Tổng thanh toán: " + formatCurrency(totalPayment))
                .setFont(boldFont)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(12)
                .setMarginBottom(20));
    }

    /**
     * Phương thức hỗ trợ thêm phần chữ ký và lời cảm ơn vào tài liệu PDF
     */
    private void addSignatureAndThanks(Document document, PdfFont boldFont, PdfFont regularFont) {
        document.add(new Paragraph(COMPANY_NAME)
                .setFont(boldFont)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(10));
        document.add(new Paragraph("Cảm ơn quý khách!")
                .setFont(regularFont)
                .setTextAlignment(TextAlignment.RIGHT)
                .setFontSize(8));
    }

    /**
     * Tính tổng số tiền đơn hàng từ chi tiết
     */
    private BigDecimal calculateOrderTotal(List<OrderDetailDTO.GetOrderDetailDTO> details) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderDetailDTO.GetOrderDetailDTO detail : details) {
            BigDecimal lineTotal = detail.getUnitPrice()
                    .multiply(BigDecimal.valueOf(detail.getQuantity()));
            totalAmount = totalAmount.add(lineTotal);
        }

        return totalAmount;
    }

    /**
     * Định dạng tiền tệ theo locale Việt Nam
     */
    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0 VNĐ";
        }
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(VIETNAM_LOCALE);
        return currencyFormatter.format(amount);
    }

    /**
     * Định dạng ngày tháng
     */
    private String formatDateTime(Object dateTime) {
        if (dateTime == null) {
            return "";
        }
        if (dateTime instanceof java.time.LocalDateTime) {
            return ((java.time.LocalDateTime) dateTime).format(DATE_FORMATTER);
        }
        if (dateTime instanceof String) {
            return (String) dateTime;
        }
        return dateTime.toString();
    }

    /**
     * Chuyển đổi enum PaymentMethod sang tên hiển thị
     */
    private String getPaymentMethodName(String method) {
        switch (method) {
            case "PAYPAL": return "PayPal";
            case "CASH": return "Tiền mặt";
            case "BALANCEACCOUNT": return "Ví THAPV Pharmacity";
            default: return method;
        }
    }

    /**
     * Chuyển đổi enum OrderStatus sang tên hiển thị
     */
    private String getOrderStatusName(String status) {
        switch (status) {
            case "PENDING": return "Đang xử lý";
            case "CONFIRMED": return "Đã xác nhận";
            case "COMPLETED": return "Đã hoàn thành";
            case "CANCELLED": return "Đã hủy";
            default: return status;
        }
    }

    /**
     * Chuyển đổi enum ServiceBookingStatus sang tên hiển thị
     */
    private String getServiceBookingStatusName(String status) {
        switch (status) {
            case "PENDING": return "Đang chờ xác nhận";
            case "CONFIRMED": return "Đã xác nhận";
            case "COMPLETED": return "Đã hoàn thành";
            case "CANCELLED": return "Đã hủy";
            default: return status;
        }
    }

    /**
     * Xây dựng URL đặt lại mật khẩu
     */
    private String buildResetPasswordUrl(String resetToken) {
        return frontendUrl + "/forgot-password?token=" + resetToken;
    }

    /**
     * Xây dựng URL xác minh tài khoản
     */
    private String buildVerificationUrl(String verificationToken) {
        return frontendUrl + "/verify?token=" + verificationToken;
    }

    /**
     * Giao diện chức năng cho việc tạo PDF
     */
    @FunctionalInterface
    private interface PdfGenerator {
        byte[] generate() throws Exception;
    }
}