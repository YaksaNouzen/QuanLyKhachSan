# Module Quan ly phong - Nhom 2

Module Java Swing/SQL Server doc lap, mo thu muc nay bang NetBeans.

## Chay nhanh

- Yeu cau JDK 17 tro len.
- Mo project `nhom 2 quan ly phong` trong NetBeans; class chay la `app.PhongMainView`.
- Demo khong can SQL Server: chay `run-demo.cmd` hoac build roi chay `app.PhongMainView --demo`.
- SQL Server: chay `app.PhongMainView` khong co tham so. Ung dung hoi JDBC URL, username, password va kiem tra schema truoc khi mo giao dien.
- Script build/test: `./build.ps1`, `./test.ps1`.

Driver JDBC SQL Server khong commit vao Git. Dat file driver trong `lib/` theo ten trong `nbproject/project.properties`, hoac dat bien `HOTEL_JDBC_JAR` khi chay test. Cau hinh ket noi co the dung `HOTEL_DB_URL`, `HOTEL_DB_USERNAME`, `HOTEL_DB_PASSWORD` hoac system properties `hotel.db.url`, `hotel.db.username`, `hotel.db.password`.

## Database

Schema nen hien nam tren nhanh `QuanLyDatPhong` cua repo nhom, tai `database/schema.sql`; tai thoi diem port no chua co trong `origin/main`. Database can co cac bang `Phong`, `LoaiPhong`, `Tang`, `DatPhong`, `VeSinh`, `BaoTri` va database user `hotelbooking_app`.

Tren database thu nghiem, chay `sql/patches/N2_001_buong_phong_va_bao_tri.sql` roi `N2_002_grant_quyen_nhom2.sql`. Xem [sql/README.md](sql/README.md) truoc khi chay. Khong chay patch len database dung chung neu chua duoc truong nhom duyet.

## Pham vi module

- `module.phong`: CRUD phong, loai phong, tang; loc danh sach; so do; ngung su dung mem.
- `module.buongphong`: phan cong, bat dau, xac nhan hoan thanh va huy viec don.
- `module.baotri`: bao hong, bat dau sua, ghi chi phi/ket qua, hoan tat va huy phieu chua sua.
- Khong tao/huy booking, check-in/check-out hay doi phong. Hai thao tac tac nghiep doc `DatPhong` de chan don/sua khi phong dang co khach hoac con booking theo cac guard da dinh.
- Tab ho so, tien nghi, review, yeu cau va phan anh duoc giao module khac theo phan cong cho repo nhom nay; `ModulePhong-current` cua repo rieng van giu pham vi/hop dong cu.

## Cau truc

```text
src/
  app/PhongMainView.java
  common/database/DatabaseConnection.java
  common/database/ConnectionFactory.java
  common/database/SchemaCheckNhom2.java
  common/ui/HotelTheme.java
  module/phong/{model,dao,service,controller,view}/
  module/buongphong/{model,dao,service,controller,view}/
  module/baotri/{model,dao,service,controller,view}/
test/
sql/patches/
```

Ba panel duoc ghep vao `JTabbedPane` cua `PhongMainView`. Lop `DatabaseConnection.java` duoc copy byte-for-byte tu nhanh `QuanLyDatPhong`; cac DAO SQL dung `ConnectionFactory` va tai su dung `PhongDAO.Session` de cap nhat phieu + trang thai phong tren cung JDBC connection.
