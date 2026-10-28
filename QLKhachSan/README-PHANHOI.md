# Module Quản lý phản hồi khách hàng

> Tài liệu lịch sử của module độc lập, không phải hướng dẫn chạy bản tích hợp.
> **Không chạy** `sql/module_phan_hoi.sql` hay tạo database `QLPhanHoi` cho bản
> hiện tại. Dùng `../database/FULL_DATABASE.sql` và hướng dẫn trong `README.md`.
> Main Class hiện tại là `app.Main`.

Module dành cho nhân viên ghi nhận đánh giá/khiếu nại. Không có giao diện khách hàng tự gửi, đăng nhập, tra cứu khách hàng chung hoặc chức năng xóa phản hồi/lịch sử.

## 1. Chuẩn bị SQL Server (thực hiện thủ công)

1. Mở SQL Server Management Studio và kết nối đúng SQL Server dự kiến sử dụng.
2. Mở `sql/module_phan_hoi.sql`, kiểm tra trước khi chạy.
3. Thực thi toàn bộ script bằng tài khoản có quyền tạo database/bảng. Script tạo database `QLPhanHoi` nếu chưa có, sau đó tạo `dbo.PhanHoi` và `dbo.LichSuXuLyPhanHoi` cùng ràng buộc/chỉ mục.
4. Nếu một trong hai bảng đã có, script dừng; không xóa/tạo lại hay sửa bảng hiện có. Không cần chạy lại sau khi cài thành công.
5. Cấp quyền phù hợp cho tài khoản ứng dụng: SELECT, INSERT trên hai bảng; UPDATE trên PhanHoi. Ứng dụng không cần quyền DELETE, ALTER hoặc UPDATE lịch sử.

Không có script tự động chạy khi mở ứng dụng hoặc build. Script không tạo bảng KhachHang, không chứa dữ liệu mẫu và không tác động QLDichVu/QLHDKhachSan. Script chưa được thực thi trên SQL Server trong lần triển khai này.

`MaKhachHang VARCHAR(20)` bắt buộc, hiện chỉ là mã tham chiếu do nhân viên nhập. Chưa kiểm tra mã đó có tồn tại ở hệ thống khách hàng; chưa có khóa ngoại khách hàng. FK nội bộ từ lịch sử đến phản hồi đã có, không xóa dây chuyền.

## 2. Cấu hình kết nối riêng

Chỉ module mới đọc các biến môi trường sau. Không sửa hai DBConnection cũ.

| Biến | Giá trị |
| --- | --- |
| QLPHANHOI_DB_HOST | Bắt buộc: tên máy hoặc địa chỉ SQL Server, không kèm tiền tố JDBC |
| QLPHANHOI_DB_PORT | Cổng TCP, mặc định 1433; với named instance dùng cổng TCP thực tế của instance |
| QLPHANHOI_DB_USER | Bắt buộc: tài khoản SQL Server có quyền vào QLPhanHoi |
| QLPHANHOI_DB_PASSWORD | Bắt buộc: mật khẩu của tài khoản đó |
| QLPHANHOI_DB_TRUST_CERT | Mặc định false: xác minh chứng chỉ máy chủ; với SQL Server phát triển dùng chứng chỉ tự ký, có thể đặt true nếu tin cậy máy chủ |

Kết nối luôn yêu cầu mã hóa (`encrypt=true`). Tên database cố định `QLPhanHoi`; timeout đăng nhập 8 giây, timeout socket/truy vấn 15 giây. Không hard-code hoặc in mật khẩu ra log.

Thiết lập biến qua Windows “Edit environment variables for your account”, rồi thoát/mở lại NetBeans để chương trình Java nhận môi trường mới. Không đưa mật khẩu vào source, README, tham số dòng lệnh hay commit. Nếu chưa cấu hình hoặc chưa tạo bảng, module báo lỗi và cho tải lại; các module cũ không đổi cấu hình.

## 3. Mở và sử dụng

- Bản tích hợp hiện tại: mở project `QLKhachSan`, Main Class `app.Main`.
- Hoặc `java -jar dist/QLKhachSanTichHop.jar`, giữ `dist/lib` bên cạnh JAR.
- Chọn “Quản lý phản hồi khách hàng” dưới “Quản lý dịch vụ”.
- Tiếp nhận mới: nhập mã khách tối đa 20 ký tự, loại Đánh giá/Khiếu nại, nội dung tối đa 2000 ký tự, thời gian `dd/MM/yyyy HH:mm:ss`, người tiếp nhận tùy chọn tối đa 100 ký tự.
- Thời gian mặc định hiện tại, cho phép thời gian tiếp nhận quá khứ, không chấp nhận tương lai. Đây là giờ địa phương; máy ứng dụng và SQL Server nên thống nhất múi giờ.
- Tìm theo mã phản hồi chính xác, mã khách hoặc nội dung chứa từ khóa. Kết hợp lọc loại và trạng thái; các ký tự `%`, `_`, `[` trong từ khóa được hiểu là ký tự thông thường.
- Chọn dòng để xem toàn bộ nội dung. Cập nhật xử lý: nhập ghi chú bắt buộc và chọn Đang xử lý/Đã xử lý. Có thể tiếp tục ghi tiến độ khi vẫn Đang xử lý.
- Phản hồi Đã xử lý chỉ được tra cứu, không mở lại.
- Xem lịch sử: chọn từng dòng lịch sử để đọc toàn bộ ghi chú. Tên người thực hiện là nhập thủ công, không phải danh tính đã xác thực; trống hiển thị “Chưa ghi nhận”.
- Làm mới giữ bộ lọc hiện tại; Bỏ lọc xóa từ khóa và đưa loại/trạng thái về tất cả.

## 4. Kiến trúc và bảo toàn lịch sử

View → PhanHoiController → PhanHoiService → DAO → JDBC → SQL Server.

PhanHoiService mở transaction và truyền cùng Connection cho PhanHoiDAO/LichSuXuLyPhanHoiDAO. Tạo phản hồi + lịch sử tiếp nhận hoặc cập nhật trạng thái + lịch sử xử lý cùng commit/rollback. ROWVERSION kiểm tra cập nhật đồng thời; dữ liệu cũ không được ghi đè. Sau xung đột, đóng dialog, bấm Làm mới và mở xử lý lại. Khi mất kết nối đúng lúc lưu, hãy tải lại kiểm tra trước khi thử lưu lần nữa.

Tác vụ JDBC chạy qua Controller/Service trên SwingWorker, không nằm trong View và không chặn luồng giao diện. Các nút lưu bị khóa khi đang xử lý để tránh nhấn lặp.

## 5. Khi tích hợp database khách hàng sau này

Giữ nguyên mã VARCHAR(20), đối chiếu mã đã nhập và xử lý các mã không tồn tại trước. Bổ sung tra cứu/kiểm tra khách hàng tại Service và DAO, không đưa SQL vào View. Sau khi chuyển các bảng vào cùng database với KhachHang và xác nhận collation/kiểu dữ liệu phù hợp, chuẩn bị migration riêng tạo FK đến dbo.KhachHang(MaKhachHang). Không giả định FK xuyên database; không có bước này trong script hiện tại.

## 6. Kiểm chứng

- Build Ant thành công trên JDK 25.0.2, source/target 21 theo cấu hình có sẵn. Chưa chạy trên JDK 21; cảnh báo compiler về source/target so với --release thuộc cấu hình build hiện có.
- `verification/phanhoi/PhanHoiTest.java`: dùng Service và DAO thật với JDBC giả lập trong bộ nhớ, không có driver SQL Server trên classpath kiểm thử. Kiểm tra validation, binding PreparedStatement, ánh xạ dữ liệu, tìm/lọc, lịch sử độc lập, rollback, ROWVERSION cũ/xung đột lúc ghi, không mở lại phản hồi hoàn tất, đóng kết nối, tải nền, điều hướng và giao diện.
- `verification/IntegrationSmokeTest.java`: kiểm tra hồi quy hai module cũ bằng JDBC giả lập.
- Ảnh ở `verification/phanhoi/`: kết xuất Swing ngoại tuyến, không phải dữ liệu khách thật.
- Chưa kiểm chứng trên SQL Server thật: thực thi DDL, quyền truy cập, chứng chỉ, kiểu ROWVERSION qua Microsoft driver, cạnh tranh transaction thực tế, lưu/đọc dữ liệu thật. Không có kết nối hoặc ghi dữ liệu thật trong các kiểm tra này.

Chạy lại kiểm tra ngoại tuyến sau build, ở thư mục project:

    powershell -File verification/phanhoi/run-tests.ps1 -JdkHome "C:\Program Files\Apache NetBeans\jdk"

Runner chỉ sử dụng classpath build/classes và verification, không nạp lib JDBC. Các biến môi trường giả chỉ tồn tại trong tiến trình kiểm thử và được khôi phục khi kết thúc.

## 7. File thay đổi

- Tạo mới `src/Module_PhanHoi/`: model (4 class/enum), Dao (ConnectionProvider, DBConnection, PhanHoiDAO, LichSuXuLyPhanHoiDAO), service (PhanHoiService), controller (PhanHoiController), view (PhanHoiPanel, PhanHoiUI, TiepNhanPhanHoiDialog, XuLyPhanHoiDialog, LichSuXuLyDialog).
- Tạo mới `sql/module_phan_hoi.sql`, tài liệu này và `verification/phanhoi/`.
- Chỉnh MainFrame: import/trường PhanHoiPanel, nút sidebar hai dòng, nhánh PHANHOI và hàm đăng ký card; không đổi menu cũ, kích thước, màu hoặc nghiệp vụ cũ.
- Bổ sung liên kết tài liệu vào README.md. Build cập nhật sản phẩm trong build/dist.

