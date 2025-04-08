package hunre.edu.vn.backend.service;

import hunre.edu.vn.backend.entity.Order;
import hunre.edu.vn.backend.entity.ServiceBooking;
import hunre.edu.vn.backend.entity.User;
import jakarta.mail.MessagingException;

public interface EmailService {
    /**
     * Gửi email xác nhận đơn hàng
     * @throws MessagingException Lỗi gửi email
     */
    void sendOrderConfirmationEmail(Long orderId, String userEmail, String userName) throws MessagingException;

    /**
     * Gửi email xác nhận dịch vụ
     * @param user Thông tin người dùng
     * @param serviceBooking Đặt dịch vụ
     * @throws MessagingException Lỗi gửi email
     */
    void sendServiceBookingConfirmationEmail(User user, ServiceBooking serviceBooking) throws MessagingException;

    /**
     * Gửi email đặt lại mật khẩu
     * @param user Người dùng
     * @param resetToken Mã đặt lại mật khẩu
     * @throws MessagingException Lỗi gửi email
     */
    void sendPasswordResetEmail(User user, String resetToken) throws MessagingException;

    /**
     * Gửi email xác minh tài khoản
     * @param user Người dùng
     * @param verificationToken Mã xác minh
     * @throws MessagingException Lỗi gửi email
     */
    void sendVerificationEmail(User user, String verificationToken) throws MessagingException;
}