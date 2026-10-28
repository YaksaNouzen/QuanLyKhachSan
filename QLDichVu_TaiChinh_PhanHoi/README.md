# QLKhachSanTichHop

## Chạy bản tích hợp hiện tại

1. Tạo database trống bằng `../database/FULL_DATABASE.sql` theo hướng dẫn ở
   `../database/README.md`.
2. Mở chính thư mục `QLDichVu_TaiChinh_PhanHoi` bằng NetBeans và chạy project
   với Main Class `app.Main`.
3. Khi mở module lần đầu, nhập JDBC URL
   tới database `QuanLyKhachSan` và tài khoản SQL Server trong hộp kết nối.
   Thông tin này chỉ lưu trong phiên chạy; không đưa mật khẩu vào source.
4. Nếu chưa có tài khoản hệ thống nhân sự, mục Nhân viên sẽ mời tạo Admin đầu
   tiên, sau đó đăng nhập. Đây là tài khoản **ứng dụng**, khác với tài khoản
   **SQL Server** ở bước 3.

Cả 15 module dùng chung kết nối tới `QuanLyKhachSan`. Ba lớp `DBConnection`
của Tài chính, Dịch vụ và Phản hồi chỉ còn là cầu nối tương thích với mã cũ;
không tạo database riêng. Chưa dùng các script SQL đơn lẻ của từng nhóm để
tạo lại database tích hợp.

Trả phòng tự lập hóa đơn gồm tiền phòng và các dịch vụ đã ghi nhận cho đơn.
Đổi phòng khi đang ở lập ngay hóa đơn cho chặng phòng cũ; đơn phòng mới sẽ được
lập hóa đơn khi trả hoặc đổi tiếp. Tiền phòng theo giá chốt và số ngày đặt,
không giảm khi trả sớm. Ngày đổi phòng thuộc chặng mới, không cộng hai lần;
mỗi chặng không đổi phòng có tối thiểu một đêm. Nếu đơn đã có hóa đơn lập tay,
chương trình giữ hóa đơn đó và không nhập tự động các dòng lần nữa. Không đổi
schema. Chưa xác nhận chạy thực tế trên SQL Server tích hợp.

Thử luồng tổng thể: tạo khách và phòng, đặt phòng, nhận phòng, chọn dịch vụ
đang cung cấp rồi nhấn **Ghi nhận sử dụng** với mã đặt phòng, sau đó trả phòng.
Mở **Quản lý hóa đơn** để tìm theo mã đặt phòng; xem các dòng phòng/dịch vụ ở
**Chi tiết hóa đơn**, thanh toán và đối chiếu thống kê. Thử riêng đổi phòng:
đơn cũ có hóa đơn ngay, đơn mới có hóa đơn khi kết thúc chặng mới. Không dùng
các mã đặt phòng chưa ở hoặc đã trả để ghi dịch vụ.

## Cấu trúc source hiện tại

`src/app/Main.java` là điểm chạy duy nhất; `src/app/view/MainFrame.java` là
cửa sổ điều hướng chung, không thuộc module Tài chính. `src/common` chứa các
thành phần dùng chung. 15 module ngang cấp dưới `src`, tên ngắn theo chức năng:

- Lễ tân: `Module_KhachHang`, `Module_DatPhong`, `Module_NhanTraPhong`.
- Phòng: `Module_Phong`, `Module_BuongPhong`, `Module_BaoTri`.
- Nhân sự: `Module_NhanVien`, `Module_CaLamViec`, `Module_TaiKhoanPhanQuyen`.
- Kho: `Module_KhoVatTu`, `Module_NhaCungCap`, `Module_NhapHang`.
- Tài chính và khách hàng: `Module_TaiChinh`, `Module_DichVu`,
  `Module_PhanHoiKhachHang`.

Mỗi module có `model`, `dao`, `service`, `controller`, `view`; module Tài khoản
có thêm `util` cho đăng nhập. View của ba module Nhân sự đã đi qua Controller
và Service thay vì gọi DAO trực tiếp. Chỉ di chuyển package/cấu trúc và thêm
lớp chuyển tiếp; không đổi câu SQL hay schema trong lần sắp xếp này.

## Ghi chép của bản Nhóm 5 trước khi tích hợp

Các chỉ dẫn và kết quả kiểm thử dưới đây mô tả bản Nhóm 5 ban đầu, chưa phản
ánh trạng thái của toàn bộ 15 module hiện nay.

Ứng dụng Swing tích hợp, dùng Module_TaiChinh.view.MainFrame làm cửa sổ chính.

## Mở và chạy bằng NetBeans
1. File > Open Project, chọn thư mục QLKhachSanTichHop.
2. Chọn JDK 21 trở lên tại Project Properties > Libraries > Java Platform.
3. Main Class: Module_TaiChinh.view.MainFrame.
4. Clean and Build Project, sau đó Run Project (F6).
5. Chọn Quản lý dịch vụ dưới Thống kê tài chính trên sidebar.

Thư viện JDBC nằm trong lib, không phụ thuộc đường dẫn Downloads. Hai DBConnection giữ nguyên cấu hình của hai module gốc. Máy chạy cần truy cập được hai database QLDichVu và QLHDKhachSan với cấu hình tương ứng. Không có liên kết dữ liệu giữa hai module và không có thay đổi schema. Không công bố thông tin đăng nhập trong tài liệu này.

## Build và chạy ngoài IDE
Với JAVA_HOME trỏ tới JDK và Ant có trong PATH:

    ant jar
    java -jar dist/QLKhachSanTichHop.jar

Giữ thư mục dist/lib bên cạnh file JAR khi sao chép bản chạy.

## Cấu trúc
- src/Module_TaiChinh: source tài chính; chỉ MainFrame thay đổi để thêm điều hướng Dịch vụ.
- src/QLDichVu: DBConnection, model, DAO, Service, Controller và view của module Dịch vụ.
- nbproject, build.xml, manifest.mf: cấu hình NetBeans/Ant.
- lib: Microsoft JDBC driver 13.6.0, dùng chung cho hai kết nối.
- dist: JAR chạy ứng dụng và lib kèm theo.
- legacy: cửa sổ MainForm cũ và đoạn khởi chạy riêng của DichVuPanel, lưu ngoài source biên dịch.
- verification: kiểm tra ngoại tuyến, ảnh giao diện và đối chiếu file gốc.
- test/QLDichVu/DichVuLayerTest.java: kiểm thử offline luồng Controller - Service - DAO giả, không ghi SQL Server.

Các main kiểm tra kết nối trong DBConnection giữ nguyên để không sửa lớp kết nối. Điểm khởi chạy ứng dụng được cấu hình và đóng gói duy nhất là MainFrame. Các Test*.java cũ được giữ tại project tài chính gốc, không đưa vào bản phân phối.

## Phạm vi thay đổi
- MainFrame.java: import và trường DichVuPanel, nút sidebar, nhánh điều hướng, hàm mở card DICHVU.
- DichVuPanel.java: phần dựng UI, helper trang trí, màu/font/bố cục; bỏ điểm chạy JFrame độc lập khỏi source ứng dụng và lưu lại ở legacy.
- Cấu hình project: tên, main class, JDK mặc định và đường dẫn JDBC tương đối; build.xml hỗ trợ JDK hiện hành và đóng gói dist/lib.
- Không thay đổi SQL, Model hoặc DAO. Bổ sung Service và Controller còn thiếu cho Dịch vụ; DichVuPanel không gọi DAO trực tiếp nữa.
- Chỉ có lọc/tìm theo tên dịch vụ như source gốc; không thêm bộ lọc mới.

## Kiểm chứng ngày 27/10/2026
- Ant jar: BUILD SUCCESSFUL; biên dịch 29 file source.
- Môi trường có JDK 25.0.2; source/target vẫn là 21. Trình biên dịch cảnh báo chưa dùng --release 21; chưa chạy thực tế trên JDK 21.
- IntegrationSmokeTest chạy với fake JDBC trong bộ nhớ, không nạp JAR JDBC thật trên classpath kiểm tra; không kết nối SQL Server.
- Đạt: tất cả mục sidebar, chuyển qua lại, chỉ một JFrame, tái sử dụng panel Dịch vụ; danh sách/loại dịch vụ, chọn dòng, kiểm tra giá âm/rỗng/sai định dạng, làm tròn, tìm kiếm, làm mới, xóa trắng và gắn handler cho đủ 7 nút.
- Ảnh Swing kết xuất ở 1200x750 và 950x600; dữ liệu trong ảnh là giả lập.
- Đối chiếu hash: không file gốc nào thay đổi. Các file tài chính sao chép chỉ khác MainFrame. Các handler nghiệp vụ Dịch vụ giữ nguyên văn bản.
- Chưa kiểm chứng: kết nối hai SQL Server thực, CRUD ghi thật, kết quả SQL tìm kiếm trên dữ liệu thật, transaction và nghiệp vụ tài chính thực tế; thao tác desktop trực tiếp trong NetBeans. Không có thao tác thêm/sửa/xóa dữ liệu thật.

## Chạy lại kiểm tra ngoại tuyến
Chạy tại thư mục project, không thêm lib JDBC vào classpath sau:

    javac -encoding UTF-8 -cp build/classes -d verification verification/IntegrationSmokeTest.java
    java -cp "build/classes;verification" IntegrationSmokeTest

Kiểm tra tạo cửa sổ Swing không hiển thị và kết xuất ảnh; cần môi trường desktop Java (không headless). Các câu SQL ghi bị chặn trong fake JDBC. Kiểm tra không xác nhận CRUD database thật.

## Kiểm thử tầng Dịch vụ
`DichVuLayerTest` kiểm tra tải loại, thêm, sửa, tìm kiếm, ngừng/khôi phục,
chuẩn hóa dữ liệu, làm tròn `DECIMAL(18,2)` và các trường hợp dữ liệu sai.
DAO giả được dùng để bài kiểm thử chạy độc lập với SQL Server.
