# NHÓM 3 — NHÂN SỰ & HỆ THỐNG
### Tài liệu giải thích code để thuyết trình (Thành viên 3)

---

## 1. Tổng quan

| STT | Module | File giao diện | Ai được dùng |
|---|---|---|---|
| 7 | Quản lý nhân viên | `NhanVienPanel.java` | Admin (đầy đủ), Kế toán (chỉ xem) |
| 8 | Quản lý ca làm việc | `CaLamPanel.java` + `PhanCaPanel.java` | Admin tạo ca, phân ca. Mọi người xem lịch và bàn giao ca của mình |
| 9 | Tài khoản & phân quyền | `LoginForm.java`, `MainFrame.java`, `TaiKhoanPanel.java`, `DoiMatKhauPanel.java` | Đăng nhập/đổi mật khẩu: tất cả. Quản lý tài khoản: Admin |

Các bảng SQL: `NhanVien`, `CaLam`, `PhanCa`, `TaiKhoan`, `VaiTro`, nằm trong file `database/Nhom3_NhanSu.sql`.

---

## 2. Cấu trúc code: mô hình 3 lớp (Model, DAO, View)

```
src/
├── model/   → Lớp "khuôn" chứa dữ liệu (chỉ có thuộc tính + get/set)
│     NhanVien, CaLam, PhanCa, TaiKhoan, VaiTro
├── dao/     → Lớp làm việc với database (SELECT/INSERT/UPDATE/DELETE)
│     NhanVienDAO, CaLamDAO, PhanCaDAO, TaiKhoanDAO, VaiTroDAO, ThongKeDAO
├── util/    → Tiện ích dùng chung
│     DBConnection (kết nối), MaHoa (băm mật khẩu),
│     PhienDangNhap (lưu người đang đăng nhập), GiaoDien (màu, nút, khung)
└── view/    → Giao diện Swing
      LoginForm, MainFrame, TrangChuPanel, NhanVienPanel, CaLamPanel,
      PhanCaPanel, TaiKhoanPanel, DoiMatKhauPanel
```

**Khi thuyết trình có thể nói:**
> "Em chia code thành 3 lớp. **View** chỉ lo hiển thị và lấy dữ liệu người dùng nhập. **DAO** chỉ lo nói chuyện với database. **Model** là đối tượng trung gian để chuyển dữ liệu giữa hai lớp đó. Nhờ vậy khi đổi database thì chỉ sửa DAO, còn giao diện giữ nguyên."

**Luồng chạy của 1 chức năng (ví dụ bấm "Thêm nhân viên"):**
```
Người dùng bấm nút
   → View: layDuLieuForm() kiểm tra dữ liệu, tạo đối tượng NhanVien
   → DAO: dao.them(nv) chạy câu INSERT bằng PreparedStatement
   → Trả về true/false
   → View: báo JOptionPane, gọi lamMoi() để load lại bảng
```

**Vì sao dùng `PreparedStatement` thay vì cộng chuỗi SQL?**
- Chống **SQL Injection**. Nếu cộng chuỗi, người dùng gõ `' OR '1'='1` vào ô đăng nhập là vào được không cần mật khẩu.
- Không phải tự xử lý dấu nháy `'` trong tên tiếng Việt.

**Vì sao dùng `try (Connection conn = ...)`?** Đây là *try-with-resources*: hết khối lệnh thì Java tự đóng kết nối, không cần viết `conn.close()`.

---

## 3. Module 9: Đăng nhập, phân quyền, tài khoản

Phần này giải thích trước vì mọi màn hình khác đều phụ thuộc vào nó.

### 3.1. Mã hóa mật khẩu (`util/MaHoa.java`)
- Mật khẩu **không lưu dạng chữ thường** trong database mà lưu **mã băm SHA-256** (64 ký tự hex).
- `"123456"` → `8D969EEF6ECAD3C29A3A629280E686CF0C3F5D5A86AFF3CA12020C923ADC6C92`
- Băm là **một chiều**: từ mã băm không suy ngược ra mật khẩu. Vì vậy khi đăng nhập, ta **băm mật khẩu người dùng gõ rồi so sánh với mã băm đã lưu**.
- Trong file SQL, dữ liệu mẫu dùng `HASHBYTES('SHA2_256', '123456')` của SQL Server, cho ra đúng chuỗi giống hàm Java.

### 3.2. Đăng nhập (`LoginForm.dangNhap()` + `TaiKhoanDAO.dangNhap()`)
Các bước kiểm tra lần lượt:
1. **Rỗng**: chưa nhập tên hoặc mật khẩu thì báo lỗi.
2. **Không kết nối được database** (`DBConnection.kiemTraKetNoi()`): báo riêng là lỗi kết nối, **không tính vào số lần nhập sai**. Nếu không có bước này, SQL Server chưa bật cũng bị báo "sai mật khẩu" và sau 3 lần chương trình tự thoát.
3. **Sai tài khoản/mật khẩu**: DAO chạy
   `SELECT ... WHERE TenDangNhap = ? AND MatKhau = sha256(matKhauNhap)`.
   Không có dòng nào thì trả về `null` và tăng biến `soLanSai`. **Sai 3 lần thì thoát chương trình** (chống đoán mật khẩu).
4. **Tài khoản bị khóa** (`TrangThai = 0`): không cho vào.
5. **Nhân viên đã nghỉ việc**: không cho vào, dù tài khoản còn mở.
6. Hợp lệ: lưu vào `PhienDangNhap.setTaiKhoan(tk)`, mở `MainFrame`, đóng form đăng nhập.

> Câu SELECT có JOIN với `NhanVien` và `VaiTro` để lấy luôn họ tên và tên vai trò, đỡ phải truy vấn thêm.

### 3.3. Phiên đăng nhập (`util/PhienDangNhap.java`)
- Dùng **biến static** để lưu tài khoản đang đăng nhập. Static nghĩa là cả chương trình dùng chung một biến, màn hình nào cũng lấy được bằng `PhienDangNhap.getTaiKhoan()`.
- Có sẵn hàm `laAdmin()` để kiểm tra quyền nhanh.

### 3.4. Phân quyền (`MainFrame.initComponents()`)
Menu bên trái được tạo **tùy theo vai trò**:

| Menu | Admin | Lễ tân | Kế toán | Nhân viên |
|---|:-:|:-:|:-:|:-:|
| Trang chủ | ✔ | ✔ | ✔ | ✔ |
| Quản lý nhân viên | ✔ | | xem | |
| Quản lý ca làm | ✔ | | | |
| Lịch trực & bàn giao | ✔ (phân ca, xóa) | ✔ (xem, bàn giao ca mình) | ✔ | ✔ |
| Tài khoản & phân quyền | ✔ | | | |
| Đổi mật khẩu | ✔ | ✔ | ✔ | ✔ |

Phân quyền ở **2 mức**:
1. **Mức menu**: không có quyền thì không hiện nút menu, nên không mở được màn hình đó.
2. **Mức chức năng** trong màn hình:
   - `NhanVienPanel(true)`: Kế toán mở ở chế độ chỉ xem, không hiện các nút Thêm/Sửa/Xóa.
   - `PhanCaPanel`: không phải Admin thì ẩn khung "Phân lịch", nút "Phân ca" và "Xóa phân ca". Khi bàn giao ca, code kiểm tra `pc.getMaNV().equals(maNVDangNhap)` để chỉ bàn giao được ca của chính mình.

**Các nhóm khác ghép vào:** thêm `themMenu("Quản lý hóa đơn", () -> hienThi(new HoaDonPanel()));` vào đúng chỗ có comment trong `MainFrame`, đặt trong `if` của vai trò được phép (VD hóa đơn cho Lễ tân và Kế toán).

**Đổi màn hình:** hàm `hienThi(JPanel)` xóa nội dung cũ bên phải (`removeAll`), thêm panel mới, rồi gọi `revalidate()` và `repaint()` để vẽ lại. Mỗi lần bấm menu thì tạo panel mới, nên dữ liệu luôn mới nhất.

### 3.5. Đăng xuất (`MainFrame.dangXuat()`)
Hỏi xác nhận, gọi `PhienDangNhap.dangXuat()` (gán `null`), mở lại `LoginForm` và đóng `MainFrame`.

### 3.6. Đổi mật khẩu (`DoiMatKhauPanel.doiMatKhau()`)
1. Nhập đủ 3 ô.
2. **Mật khẩu cũ đúng không?** Dùng lại `dao.dangNhap(tenDN, matKhauCu)`; nếu trả về `null` là sai.
3. Mật khẩu mới có ít nhất 6 ký tự.
4. "Nhập lại" phải khớp mật khẩu mới.
5. Mật khẩu mới phải khác mật khẩu cũ.
6. Gọi `capNhatMatKhau()`; hàm này **tự băm** trước khi UPDATE.

### 3.7. Quản lý tài khoản (`TaiKhoanPanel`, chỉ Admin)
| Chức năng | Logic |
|---|---|
| **Tạo tài khoản** | Tên đăng nhập 4–30 ký tự (chữ, số, `_`), kiểm tra bằng regex `[a-zA-Z0-9_]{4,30}`. Mật khẩu ít nhất 6 ký tự. Tên đăng nhập chưa tồn tại. **Mỗi nhân viên chỉ có 1 tài khoản** (cột `MaNV` là `UNIQUE`, code cũng kiểm tra trước để báo lỗi dễ hiểu). Combobox chỉ liệt kê nhân viên đang làm. |
| **Sửa quyền / trạng thái** | Chỉ đổi vai trò và Khóa/Mở khóa. Tên đăng nhập, nhân viên, mật khẩu bị khóa không cho sửa. **Admin không tự hạ quyền hay tự khóa mình** (nếu không, hệ thống có thể mất hết Admin). |
| **Xóa** | Không cho xóa tài khoản đang đăng nhập. |
| **Đặt lại mật khẩu** | Khi nhân viên quên mật khẩu, Admin reset về `123456`. Vì mật khẩu đã băm nên Admin cũng không xem được mật khẩu cũ, chỉ đặt lại được. |

---

## 4. Module 7: Quản lý nhân viên (`NhanVienPanel` + `NhanVienDAO`)

### 4.1. Hiển thị danh sách
- `loadTable(List<NhanVien>)`: xóa hết dòng cũ (`model.setRowCount(0)`), rồi `addRow` từng nhân viên.
- Biến `danhSach` giữ danh sách đang hiện. Khi click dòng thứ `i` thì lấy `danhSach.get(i)`, nên có cả những cột không hiện trên bảng (như Địa chỉ).
- Ghi đè `isCellEditable` trả về `false` để không sửa trực tiếp trên bảng.

### 4.2. Click dòng thì đưa dữ liệu lên form (`hienThiDongDangChon`)
Đổ dữ liệu lên các ô, rồi **khóa ô Mã NV** (`setEditable(false)`) vì mã là khóa chính, không được sửa.

### 4.3. Kiểm tra dữ liệu (`layDuLieuForm`)
| Ô | Ràng buộc | Cách kiểm tra |
|---|---|---|
| Mã NV | Dạng `NV` + 3 số | regex `NV\d{3}` |
| Họ tên | Không rỗng | `isEmpty()` |
| SĐT | 10 số, bắt đầu bằng 0 | regex `0\d{9}` |
| Email | Đúng định dạng (được để trống) | regex `^[\w.]+@[\w.]+\.[a-zA-Z]{2,}$` |
| Ngày | Dạng `yyyy-MM-dd` và phải là ngày có thật | `LocalDate.parse()` ném `DateTimeParseException` khi sai định dạng **hoặc ngày không có thật** (30/02, 29/02 năm không nhuận). Không dùng thẳng `Date.valueOf()` vì hàm này tự đổi 30/02 thành 01/03 mà không báo lỗi |
| Tuổi | Đủ 18 tuổi, tính chính xác đến ngày | `Period.between(ngaySinh, LocalDate.now()).getYears()` |
| Ngày vào làm | Sau ngày sinh | `ngayVaoLam.before(ngaySinh)` |

Hợp lệ thì trả về đối tượng `NhanVien`, sai thì báo lỗi và trả về `null`. Hàm Thêm và Sửa dùng chung hàm này nên không phải viết lại phần kiểm tra.

### 4.4. Thêm, Sửa, Xóa
- **Thêm**: kiểm tra trùng mã (`dao.getById(ma) != null`) rồi mới INSERT.
- **Sửa**: phải chọn dòng trước, rồi UPDATE theo `MaNV`.
- **Xóa**: hỏi xác nhận. Nếu nhân viên **đã có tài khoản hoặc đã được phân ca** thì SQL chặn vì **khóa ngoại**, DAO trả về `false`. Khi đó chương trình gợi ý **chuyển trạng thái sang "Đã nghỉ việc"** thay vì xóa, để giữ lịch sử làm việc (cách các phần mềm thật vẫn làm, gọi là "xóa mềm").

### 4.5. Phân bộ phận, chức vụ, quản lý trạng thái
- Bộ phận: Lễ tân, Buồng phòng, Kế toán, Bảo vệ, Quản lý. Chức vụ: Trưởng bộ phận, Nhân viên, Thực tập. Cả hai chọn bằng `JComboBox` để tránh gõ sai.
- Trạng thái: **Đang làm / Nghỉ phép / Đã nghỉ việc**. Trạng thái này **ảnh hưởng tới module khác**:
  - Chỉ nhân viên **Đang làm** mới hiện trong combobox phân ca và tạo tài khoản.
  - **Đã nghỉ việc** thì không đăng nhập được.

### 4.6. Tìm kiếm (`NhanVienDAO.timKiem`)
Gộp 3 điều kiện trong **1 câu SQL**:
```sql
WHERE (MaNV LIKE ? OR HoTen LIKE ? OR SDT LIKE ?)
  AND (? = N'Tất cả' OR BoPhan = ?)
  AND (? = N'Tất cả' OR TrangThai = ?)
```
- `LIKE '%từ khóa%'`: tìm gần đúng, gõ "Việt" ra "Trần Đức Việt".
- Mẹo `(? = N'Tất cả' OR BoPhan = ?)`: nếu chọn "Tất cả" thì vế đầu đúng nên bỏ qua điều kiện lọc; nếu chọn bộ phận cụ thể thì vế đầu sai và lọc theo vế sau. Nhờ vậy không cần viết nhiều câu `if` để ghép chuỗi SQL.
- `getAll()` cũng chỉ gọi `timKiem("", "Tất cả", "Tất cả")`, dùng lại code.


### 4.7. Thống kê nhân sự trên Trang chủ (`TrangChuPanel` + `ThongKeDAO`)
Mở chương trình hoặc bấm menu **Trang chủ** sẽ thấy:
- **4 thẻ số liệu:** Nhân viên đang làm, Đang nghỉ phép, Ca làm hôm nay, Ca chưa bàn giao. Thẻ "Ca chưa bàn giao" **tô đỏ khi còn ca chưa bàn giao**, về 0 thì tô xanh, để quản lý nhìn là biết.
- **2 bảng:** số nhân viên theo bộ phận và theo trạng thái.

Logic:
- Dùng `COUNT(*)` và `GROUP BY` để **database tự đếm**, Java chỉ nhận về vài dòng kết quả, không phải tải cả danh sách nhân viên về rồi tự đếm.
  ```sql
  SELECT BoPhan, COUNT(*) AS SoLuong FROM NhanVien
  WHERE TrangThai <> N'Đã nghỉ việc'
  GROUP BY BoPhan ORDER BY SoLuong DESC, BoPhan
  ```
- Bảng theo bộ phận **không tính người đã nghỉ việc**, vì họ không còn thuộc bộ phận nào.
- "Ca chưa bàn giao" chỉ đếm ca **từ hôm nay trở về trước** (`NgayLam <= hôm nay`). Ca ngày mai chưa diễn ra nên không tính là "chưa bàn giao".
- Kết quả lưu vào `LinkedHashMap` để giữ đúng thứ tự `ORDER BY` (`HashMap` thường sẽ làm lộn thứ tự).
- Ngày hôm nay truyền từ Java (`LocalDate.now()`), không dùng hàm ngày của SQL, nên chạy giống nhau trên SQL Server và H2.
- Mỗi lần bấm menu Trang chủ là tạo panel mới, nên số liệu luôn là mới nhất.

---

## 5. Module 8: Quản lý ca làm việc

### 5.1. Tạo ca (`CaLamPanel` + `CaLamDAO`)
- Mỗi ca có Mã, Tên, Giờ bắt đầu, Giờ kết thúc (kiểu `TIME` trong SQL).
- Giờ nhập dạng `HH:mm` và kiểm tra bằng regex `([01]\d|2[0-3]):[0-5]\d` (từ 00:00 đến 23:59).
- Giờ bắt đầu không được bằng giờ kết thúc.
- Khi lưu: `Time.valueOf("06:00" + ":00")`. Khi đọc: `rs.getTime(...)` cho ra `06:00:00`, hàm `CaLam.catGio()` cắt còn `"06:00"`. Cả `CaLamDAO` và `PhanCaDAO` cùng dùng hàm này nên không phải viết lặp.
- **Ca qua đêm** (22:00 → 06:00): giờ kết thúc nhỏ hơn giờ bắt đầu. Trong `CaLam.getPhutKetThuc()` nếu kết thúc ≤ bắt đầu thì **cộng thêm 24 giờ (1440 phút)**, nên 22:00→06:00 thành 1320→1800 phút (8 tiếng).
- Bảng hiện thêm cột **Số giờ** (tính từ số phút) và **Loại ca** (Trong ngày / Qua đêm).
- Xóa ca đã được phân cho nhân viên thì bị khóa ngoại chặn, chương trình báo lỗi.

### 5.2. Phân lịch nhân viên (`PhanCaPanel.phanCa()`)
1. Chọn nhân viên (chỉ hiện người **Đang làm**), chọn ca, nhập ngày.
2. Combobox nhân viên hiện `"NV001 - Trần Đức Việt"`; hàm `layMaNV()` dùng `split(" - ")[0]` lấy ra `"NV001"`.
3. Combobox ca chứa luôn **đối tượng `CaLam`** (hiện chữ nhờ ghi đè `toString()`), nên lấy ra được giờ để kiểm tra trùng.
4. **Kiểm tra ca trùng** (mục 5.3). Không trùng thì INSERT vào `PhanCa` với trạng thái "Chưa bàn giao".
5. Trong SQL, bảng `PhanCa` còn có ràng buộc `UNIQUE (MaNV, MaCa, NgayLam)`. Dù code có sót thì database vẫn chặn được việc phân một ca hai lần.

### 5.3. Kiểm tra ca trùng (`PhanCaDAO.kiemTraTrungCa()`). Phần quan trọng nhất
**Bài toán:** một nhân viên không được làm 2 ca có giờ chồng lên nhau trong cùng một ngày.

**Cách làm:**
1. Lấy tất cả ca nhân viên đó **đã được phân trong ngày** (`getCaCuaNhanVienTrongNgay`).
2. Đổi giờ ra **số phút** để so sánh bằng số: `"06:30"` → 6×60+30 = 390.
3. Hai khoảng giờ **[A1, A2)** và **[B1, B2)** chồng nhau khi và chỉ khi:

   **A1 < B2  và  B1 < A2**

**Ví dụ:**

| Ca đã có | Ca mới | A1 < B2 ? | B1 < A2 ? | Kết quả |
|---|---|---|---|---|
| Sáng 06:00–14:00 (360–840) | Hành chính 08:00–17:00 (480–1020) | 360 < 1020 ✔ | 480 < 840 ✔ | **TRÙNG** |
| Sáng 06:00–14:00 (360–840) | Chiều 14:00–22:00 (840–1320) | 360 < 1320 ✔ | 840 < 840 ✘ | Không trùng (nối tiếp) |
| Sáng 06:00–14:00 | Sáng 06:00–14:00 | ✔ | ✔ | **TRÙNG** (phân 2 lần cùng ca) |

Nếu trùng thì hiện thông báo ghi rõ **trùng với ca nào, giờ nào**.

> Dùng dấu `<` (không phải `<=`) để ca kết thúc lúc 14:00 và ca bắt đầu lúc 14:00 **không** bị tính là trùng, vì hai ca nối tiếp nhau là hợp lệ.

Chương trình kiểm tra lịch của cả ngày trước, ngày hiện tại và ngày sau nên ca đêm
22:00–06:00 cũng được đối chiếu với ca sáng của ngày kế tiếp.

### 5.4. Xem lịch trực (`PhanCaDAO.getLichTruc()`)
- Lọc **từ ngày → đến ngày** (`BETWEEN`) và theo nhân viên (hoặc "Tất cả").
- `JOIN` 3 bảng `PhanCa`, `NhanVien`, `CaLam` để hiện họ tên, tên ca, giờ làm.
- Sắp xếp `ORDER BY NgayLam, GioBatDau` để lịch hiện theo đúng thứ tự thời gian.
- Nút **Hôm nay** đặt cả 2 ô ngày bằng ngày hiện tại. Mở màn hình là tự xem lịch hôm nay.
- Kiểm tra "từ ngày" không được lớn hơn "đến ngày".

### 5.5. Bàn giao ca (`PhanCaPanel.banGiaoCa()`)
Khi hết ca, nhân viên ghi lại nội dung bàn giao cho ca sau (tiền quỹ, phòng đang có khách, việc còn tồn…).

Điều kiện kiểm tra theo thứ tự:
1. Phải chọn 1 dòng trên bảng.
2. **Chỉ người làm ca đó hoặc Admin** mới được bàn giao (phân quyền mức chức năng).
3. Ca **đã bàn giao** rồi thì không bàn giao lại.
4. Ca **chưa diễn ra** (ngày làm sau hôm nay) thì chưa bàn giao được.
5. Nội dung bàn giao không được rỗng.

Hợp lệ thì UPDATE `NoiDungBanGiao` và đổi `TrangThai = 'Đã bàn giao'`.
Ca **đã bàn giao thì không xóa được** (giữ làm bằng chứng).

---

## 6. Cách chạy trên NetBeans

1. **Tạo database**: mở SQL Server Management Studio, chạy file `database/Nhom3_NhanSu.sql` (sửa `USE QuanLyKhachSan` thành tên database chung của nhóm nếu khác).
2. **NetBeans**: File → New Project → Java with Ant → Java Application (bỏ tick *Create Main Class*).
3. Copy 4 thư mục `model`, `dao`, `util`, `view` vào thư mục `src` của project.
4. Thêm driver: chuột phải **Libraries** → *Add JAR/Folder* → chọn `mssql-jdbc-12.x.x.jre11.jar` (tải từ trang Microsoft JDBC Driver for SQL Server).
5. Cấu hình các biến môi trường `HOTEL_DB_SERVER`, `HOTEL_DB_NAME`,
   `HOTEL_DB_USER`, `HOTEL_DB_PASSWORD`; không ghi mật khẩu trực tiếp trong source.
6. Chuột phải `view/LoginForm.java` → **Run File**. (Hoặc Project Properties → Run → Main Class = `view.LoginForm`.)
7. Đăng nhập thử bằng các tài khoản mẫu, mật khẩu đều là **123456**:
   | Tên đăng nhập | Nhân viên | Bộ phận | Vai trò |
   |---|---|---|---|
   | `admin` | NV001 – Trần Đức Việt | Quản lý (Trưởng bộ phận) | Admin, thấy đủ menu |
   | `letan` | NV002 – Duy Nguyễn | Lễ tân | Lễ tân |
   | `ketoan` | NV003 – Doãn Công Trường | Kế toán | Kế toán |
   | `nhanvien` | NV004 – Nguyễn Tuấn Đạt | Buồng phòng (đang nghỉ phép) | Nhân viên |
   | *(chưa có)* | NV005 – Nguyễn Thế Anh | Bảo vệ | dùng để demo tạo tài khoản |

> **Nếu nhóm đã có project chung:** chỉ copy các file của nhóm 3 vào, dùng lớp kết nối chung của nhóm (thay `DBConnection.getConnection()` bằng hàm của nhóm), và lấy `LoginForm` làm màn hình mở đầu cho cả phần mềm.

---

## 7. Kịch bản demo khi thuyết trình (khoảng 5 phút)

1. Đăng nhập sai 1 lần, cho thấy thông báo "lần 1/3".
2. Đăng nhập `admin` → giới thiệu menu đầy đủ.
3. **Nhân viên**: thêm NV006 với SĐT sai để thấy báo lỗi, sửa lại rồi thêm thành công. Tìm "Duy", lọc bộ phận "Lễ tân". Thử xóa NV002 để thấy thông báo gợi ý chuyển "Đã nghỉ việc".
4. **Ca làm**: cho xem Ca đêm có Loại = "Qua đêm".
5. **Lịch trực**: phân NV002 (Duy Nguyễn) vào **Ca hành chính** hôm nay → báo **trùng với Ca sáng**. Phân vào **Ca chiều** → thành công (nối tiếp, không trùng).
6. **Tài khoản**: tạo tài khoản cho NV005 (Nguyễn Thế Anh, chưa có tài khoản). Thử tự khóa `admin` để thấy bị chặn.
7. Đăng xuất → đăng nhập `letan` → menu chỉ còn 3 mục. Thử bàn giao ca của người khác thì bị chặn, bàn giao ca của mình thì thành công.
8. Đổi mật khẩu → đăng xuất → đăng nhập bằng mật khẩu mới.

---

## 8. Câu hỏi thầy cô hay hỏi và gợi ý trả lời

**Hỏi: Tại sao không lưu mật khẩu thật trong database?**
Đáp: Nếu database bị lộ thì kẻ gian có ngay mật khẩu của mọi người. Lưu mã băm SHA-256 thì không suy ngược ra được. Khi đăng nhập em băm mật khẩu nhập vào rồi so sánh với mã băm đã lưu.

**Hỏi: Làm sao phân quyền?**
Đáp: Bảng `TaiKhoan` có cột `MaVaiTro` nối với bảng `VaiTro`. Đăng nhập xong em lưu tài khoản vào lớp `PhienDangNhap` (biến static). `MainFrame` đọc vai trò để quyết định hiện menu nào, trong từng màn hình còn kiểm tra thêm ở mức nút bấm.

**Hỏi: Kiểm tra ca trùng thế nào?**
Đáp: Đổi giờ ra phút, rồi dùng công thức A1 < B2 và B1 < A2 (xem mục 5.3).

**Hỏi: Xóa nhân viên đã có lịch làm thì sao?**
Đáp: SQL có khóa ngoại nên không cho xóa. Chương trình bắt lỗi và gợi ý chuyển sang "Đã nghỉ việc" để vẫn giữ lịch sử.

**Hỏi: PreparedStatement khác Statement chỗ nào?**
Đáp: PreparedStatement dùng dấu `?` rồi gán giá trị bằng `setString`, `setDate`. Cách này chống SQL Injection và xử lý được ký tự đặc biệt.

**Hỏi: Ca đêm 22h đến 6h tính thế nào?**
Đáp: Giờ kết thúc nhỏ hơn giờ bắt đầu nên em cộng thêm 24 giờ. 22:00 là 1320 phút, 06:00 thành 360 + 1440 = 1800 phút, ca dài 8 tiếng.

---

## 9. Chế độ chạy thử (khi máy chưa có SQL Server)

- Khi đăng nhập mà không kết nối được SQL Server, chương trình hỏi: *"Bạn có muốn CHẠY THỬ với dữ liệu mẫu không?"*
- Chọn **Yes** thì `DBConnection.batCheDoChayThu()` bật **H2**, một database chạy trong bộ nhớ (thư viện `lib/h2-2.3.232.jar`). H2 chạy file `util/du_lieu_chay_thu.sql` để tạo đúng 5 bảng và dữ liệu mẫu, lịch làm tính theo ngày hôm nay.
- Các lớp **DAO không sửa gì**, vì mọi DAO đều lấy kết nối qua `DBConnection.getConnection()`. Hàm này trả về kết nối H2 khi đang chạy thử, trả về SQL Server khi chạy thật. Đây là lợi ích của việc tách riêng lớp kết nối.
- Tiêu đề cửa sổ chính hiện chữ **(CHẠY THỬ - dữ liệu mẫu)**. Tắt chương trình là dữ liệu mất.
- Khi có SQL Server thật thì không hiện câu hỏi này nữa, chương trình chạy bình thường.

---

## 10. Các trường hợp đã chạy thử (153 trường hợp, tất cả đạt, chạy lại 3 lần đều đạt)

**Phần 1: kiểm tra trên form, chưa cần database (63 trường hợp)**

| Màn hình | Đã thử |
|---|---|
| Đăng nhập | Bỏ trống; thiếu mật khẩu; mất database 4 lần liên tiếp (báo lỗi kết nối, không tính là nhập sai, không thoát) |
| Phân quyền | Menu đúng cho cả 4 vai trò; bấm lần lượt mọi menu không lỗi; Kế toán không thấy nút Thêm/Sửa/Xóa nhân viên; Lễ tân không thấy nút Phân ca/Xóa phân ca |
| Nhân viên | Bỏ trống; mã sai (AB12); mã viết thường (nv005, được chấp nhận); SĐT sai; email sai; email có dấu `-`; ngày sai định dạng; ngày 30/02; còn 1 ngày nữa mới đủ 18 tuổi; vừa đủ 18 tuổi; ngày vào làm trước ngày sinh; Sửa/Xóa khi chưa chọn dòng; Làm mới; Tìm kiếm; Bỏ lọc |
| Ca làm | Bỏ trống; giờ `6h`; giờ `24:00`; giờ bắt đầu = giờ kết thúc; ca qua đêm 23:00–07:00; Sửa/Xóa khi chưa chọn |
| Lịch trực | Phân ca khi chưa có dữ liệu; ngày sai; 29/02/2026 (năm không nhuận); 29/02/2028 (năm nhuận); từ ngày > đến ngày; nút Hôm nay; Bàn giao/Xóa khi chưa chọn |
| Kiểm tra ca trùng | Sáng–Hành chính (trùng); Sáng–Chiều (nối tiếp, không trùng); Sáng–Sáng (trùng); Chiều–Đêm (không trùng); ca đêm dài đúng 8 tiếng |
| Tài khoản | Bỏ trống; tên quá ngắn; tên có dấu cách; mật khẩu < 6 ký tự; Sửa/Xóa/Đặt lại mật khẩu khi chưa chọn |
| Đổi mật khẩu | Bỏ trống; mật khẩu mới < 6 ký tự; nhập lại không khớp; mật khẩu mới trùng mật khẩu cũ; mất database; nút Nhập lại |
| Mã hóa | SHA-256 của `123456` trong Java ra đúng chuỗi mà `HASHBYTES` của SQL Server tạo |

**Phần 2: chạy với database thật ở chế độ chạy thử (80 trường hợp)**

| Chức năng | Đã thử |
|---|---|
| Đăng nhập | Bật chế độ chạy thử; sai mật khẩu đếm lần 1/3; đăng nhập đủ 4 vai trò; tài khoản bị khóa; nhân viên đã nghỉ việc; đăng xuất |
| Nhân viên | Thêm NV006; trùng mã NV001; chọn dòng đưa lên form (kể cả địa chỉ không hiện trên bảng); sửa tên và trạng thái; tìm theo tên, theo SĐT; lọc trạng thái, lọc bộ phận; bỏ lọc; xóa NV002 bị khóa ngoại chặn; xóa NV006 |
| Ca làm | Ca đêm hiện 8 giờ, loại Qua đêm; thêm ca qua đêm; trùng mã; sửa; xóa ca đã phân bị chặn; xóa ca chưa dùng |
| Lịch trực | Mở ra thấy lịch hôm nay; combo chỉ có nhân viên Đang làm; **báo trùng ca** (Sáng với Hành chính, Sáng với Sáng); ca nối tiếp được phân; lịch sắp theo ngày và giờ; lọc theo nhân viên |
| Bàn giao | Nội dung rỗng; bàn giao thành công; bàn giao lại; xóa ca đã bàn giao bị chặn; ca ngày mai chưa bàn giao được; Lễ tân bàn giao ca người khác bị chặn, ca của mình thì được |
| Tài khoản | Trùng tên đăng nhập; nhân viên đã có tài khoản; tạo mới (mật khẩu lưu dạng băm); Admin tự khóa, tự hạ quyền, tự xóa đều bị chặn; khóa tài khoản khác; đặt lại mật khẩu; xóa |
| Đổi mật khẩu | Mật khẩu cũ sai; đổi thành công; mật khẩu mới đăng nhập được, mật khẩu cũ không dùng được nữa |
| Thống kê (10 trường hợp) | Số liệu ban đầu đúng (4 đang làm, 1 nghỉ phép, 2 ca hôm nay, 2 ca chưa bàn giao); thêm người đã nghỉ việc thì không tính vào bộ phận; Lễ tân 2 người xếp đầu bảng; ca hôm qua chưa bàn giao vẫn được đếm, ca ngày mai thì không; bàn giao hết thì về 0 |

*Chưa chạy trên SQL Server thật:* file `database/Nhom3_NhanSu.sql` (lệnh `HASHBYTES`, `IF DB_ID`) chưa được chạy thử trên SQL Server. Cấu trúc bảng và các câu SQL trong DAO thì đã chạy được trên H2 ở chế độ giả lập SQL Server.
