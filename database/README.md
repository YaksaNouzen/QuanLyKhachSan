# Database tích hợp `QuanLyKhachSan`

Thư mục này là schema chung cho 15 module. Bản đầu không xóa dữ liệu, không
chứa tài khoản/mật khẩu và không trộn dữ liệu mẫu vào schema.

## Cách dựng database mới

1. Mở `FULL_DATABASE.sql` bằng SQL Server Management Studio.
2. Kết nối bằng tài khoản có quyền tạo database.
3. Chạy toàn bộ file một lần.
4. Kiểm tra thông báo cuối: 31 bảng, 20 sequence và 8 thủ tục. Trong đó có
   `dbo.sp_LayMaTuDong` và 7 thủ tục nghiệp vụ đặt/nhận/trả/đổi phòng.

Không chạy file này đè lên database đang có bảng. Database đã có dữ liệu phải
dùng patch mới trong `database/patches`.

## Tạo dữ liệu phòng để thử đặt phòng

Sau khi dựng schema, có thể chạy riêng `SEED_70_PHONG.sql` trong SSMS. File tạo
tầng 2–8, ba hạng STD/DLX/PRS và 70 phòng: STD 14 phòng/tầng ở tầng 2–4,
DLX 8 phòng/tầng ở tầng 5–7, PRS 4 phòng ở tầng 8. Giá niêm yết/đêm lần lượt
là 1.000.000, 7.000.000 và 15.000.000 VND; sức chứa tối đa 2, 4 và 7 người.
File không xóa hoặc ghi đè phòng có sẵn; nếu số phòng hay hạng hiện có khác
cấu hình này, toàn bộ lần chạy sẽ dừng và hoàn tác. Đọc danh sách `SELECT`
cuối file để kiểm tra đủ 70 phòng trước khi thử đặt phòng.
Nếu đã có hạng tương đương với mã khác (ví dụ `STD001`), file dùng lại hạng đó
khi giá và sức chứa khớp, không tự đổi mã hạng cũ.

## Cấp mã tự động

Các bảng dùng khóa `INT/BIGINT IDENTITY` tự nhận mã khi `INSERT`, gồm
`LoaiDichVu`, `DichVu`, `PhanCa`, `PhanHoi`, `LichSuXuLyPhanHoi`.

Các khóa chuỗi hiện đang được code Java sử dụng lấy qua một thủ tục chung:

```sql
EXEC dbo.sp_LayMaTuDong @LoaiMa = 'KHACH_HANG';
-- KH000001

EXEC dbo.sp_LayMaTuDong @LoaiMa = 'HOA_DON';
-- HD000001
```

Loại mã được hỗ trợ:

`KHACH_HANG`, `DAT_PHONG`, `LUOT_LUU_TRU`, `YEU_CAU_DAT_PHONG`,
`CHUYEN_PHONG`, `PHONG`, `LOAI_PHONG`, `TANG`, `TIEN_NGHI`, `VE_SINH`, `BAO_TRI`,
`SU_DUNG_DICH_VU`, `NHAN_VIEN`, `CA_LAM`, `VAT_TU`, `NHA_CUNG_CAP`,
`PHIEU_NHAP`, `HOA_DON`, `CHI_TIET_HOA_DON`, `THANH_TOAN`.

Sequence bảo đảm hai máy không nhận cùng một mã. Số có thể bị nhảy nếu người
dùng lấy mã rồi hủy thao tác; đây là hành vi bình thường của sequence và không
ảnh hưởng tính duy nhất.

## Chống trùng nghiệp vụ

Khóa chính chỉ chống trùng mã. Các ràng buộc sau chống tạo hai bản ghi cho cùng
một thực thể:

| Thực thể | Điều kiện không được trùng |
|---|---|
| Khách hàng | Loại giấy tờ + quốc gia cấp + số giấy tờ |
| Nhân viên | Số giấy tờ; email nếu có |
| Nhà cung cấp | Mã số thuế nếu có |
| Phòng | Số phòng |
| Tầng | Tên tầng |
| Loại phòng | Tên loại phòng |
| Loại dịch vụ | Tên loại dịch vụ |
| Dịch vụ | Loại dịch vụ + tên dịch vụ |
| Vật tư | Tên vật tư + đơn vị tính |
| Ca làm | Tên ca; cặp giờ bắt đầu/kết thúc |
| Tài khoản | Mỗi nhân viên tối đa một tài khoản |
| Phân ca | Nhân viên + ca + ngày làm |

Không đặt `UNIQUE` cho hóa đơn, đặt phòng, phiếu nhập, thanh toán hay phản hồi,
vì các nghiệp vụ này hợp lệ khi lặp lại theo thời gian.

## Việc cần làm ở Java

Schema mới là nền tảng; giao diện chưa tự động thay đổi. Ở bước tích hợp Java,
DAO thêm mới sẽ gọi `sp_LayMaTuDong`, bỏ ô nhập mã khỏi chế độ thêm và nhận mã
vừa cấp để hiển thị lại. Tất cả `DBConnection` cũng phải trỏ về database
`QuanLyKhachSan`.
