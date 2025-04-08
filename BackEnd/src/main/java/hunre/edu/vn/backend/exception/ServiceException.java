package hunre.edu.vn.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Ngoại lệ tùy chỉnh được sử dụng cho các lỗi xảy ra trong các lớp Service.
 * Ngoại lệ này giúp phân biệt lỗi nghiệp vụ với các lỗi khác và cung cấp
 * thông tin chi tiết về lỗi để hỗ trợ gỡ lỗi và xử lý ngoại lệ.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ServiceException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Mã lỗi tùy chỉnh để phân loại loại lỗi
   */
  private String errorCode;

  /**
   * Dữ liệu bổ sung liên quan đến lỗi
   */
  private Object errorData;

  /**
   * Tạo một ngoại lệ dịch vụ mới với thông báo lỗi
   *
   * @param message Thông báo mô tả lỗi
   */
  public ServiceException(String message) {
    super(message);
  }

  /**
   * Tạo một ngoại lệ dịch vụ mới với thông báo lỗi và nguyên nhân gốc
   *
   * @param message Thông báo mô tả lỗi
   * @param cause Nguyên nhân gốc của lỗi
   */
  public ServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  /**
   * Tạo một ngoại lệ dịch vụ mới với thông báo lỗi và mã lỗi
   *
   * @param message Thông báo mô tả lỗi
   * @param errorCode Mã số để phân loại loại lỗi
   */
  public ServiceException(String message, String errorCode) {
    super(message);
    this.errorCode = errorCode;
  }

  /**
   * Tạo một ngoại lệ dịch vụ mới với thông báo lỗi, mã lỗi và nguyên nhân
   *
   * @param message Thông báo mô tả lỗi
   * @param errorCode Mã số để phân loại loại lỗi
   * @param cause Nguyên nhân gốc của lỗi
   */
  public ServiceException(String message, String errorCode, Throwable cause) {
    super(message, cause);
    this.errorCode = errorCode;
  }

  /**
   * Tạo một ngoại lệ dịch vụ mới với thông báo lỗi, mã lỗi và dữ liệu lỗi
   *
   * @param message Thông báo mô tả lỗi
   * @param errorCode Mã số để phân loại loại lỗi
   * @param errorData Dữ liệu bổ sung liên quan đến lỗi
   */
  public ServiceException(String message, String errorCode, Object errorData) {
    super(message);
    this.errorCode = errorCode;
    this.errorData = errorData;
  }

  /**
   * Lấy mã lỗi
   *
   * @return Mã lỗi liên quan đến ngoại lệ
   */
  public String getErrorCode() {
    return errorCode;
  }

  /**
   * Đặt mã lỗi
   *
   * @param errorCode Mã lỗi liên quan đến ngoại lệ
   */
  public void setErrorCode(String errorCode) {
    this.errorCode = errorCode;
  }

  /**
   * Lấy dữ liệu lỗi
   *
   * @return Dữ liệu bổ sung liên quan đến lỗi
   */
  public Object getErrorData() {
    return errorData;
  }

  /**
   * Đặt dữ liệu lỗi
   *
   * @param errorData Dữ liệu bổ sung liên quan đến lỗi
   */
  public void setErrorData(Object errorData) {
    this.errorData = errorData;
  }
}