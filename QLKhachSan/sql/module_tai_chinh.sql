/*
  MODULE TAI CHINH - HOA DON / CHI TIET / THANH TOAN
  Project: QLKhachSanTichHop

  DA DOI CHIEU SOURCE: HoaDonDAO, ChiTietHoaDonDAO, ThanhToanDAO,
  ThongKeTaiChinhDAO va toan bo Model / Service Tai chinh.
  Database khop Module_TaiChinh.Dao.DBConnection: QLHDKhachSan.
  Cach dung: mo file UTF-8 trong SSMS, ket noi may dich va Execute ca file.
  Tai khoan can quyen tao database (neu chua co), bang va index.
  Ung dung can quyen SELECT / INSERT / UPDATE / DELETE tren ba bang tai chinh
  va SELECT tren DatPhong; dung schema mac dinh dbo de khop SQL cua DAO.

  PHAM VI VA DIEU KIEN TRIEN KHAI:
  - Chi tao bang neu chua ton tai; khong DROP/ALTER bang hien co.
    Day la script cai dat moi, KHONG phai migration. Neu da chay ban cu
    (MoTaKhoanPhi), script nay KHONG tu doi cot hoac chuyen du lieu cu.
    Ket qua kiem tra cuoi file cho biet cot DAO can nhung dang thieu.
  - Khong tao bang DatPhong, KhachHang, DichVu (thuoc module khac).
  - Chua gan FK cho MaDatPhong, MaPhong, MaDichVu do cac module con tach DB.
  - HoaDonDAO.search(5 tham so) LUON INNER JOIN DatPhong, ke ca khi
    khong loc MaKhachHang. Can cung cap DatPhong trong QLHDKhachSan voi
    MaDatPhong va MaKhachHang tu module Dat phong truoc khi dung tim kiem nay.
    Chua co DDL chinh thuc: khong suy doan kieu/PK/FK hay tao bang gia.
    MaDatPhong phai so sanh duoc voi HoaDon.MaDatPhong; moi ma nen anh xa
    mot dong dat phong. Hoa don khong co dong dat phong khop se bi loai
    khoi ket qua INNER JOIN. Chi co bang rong cung chua du de tim kiem.
  - Do dai chuoi va DECIMAL(18,2) giu theo script duoc cung cap;
    Java String/double KHONG xac dinh do dai hay precision cua SQL Server.
    Gioi han: ma 30, mo ta 500, loai/trang thai/phuong thuc 50 ky tu;
    tien toi da 9999999999999999.99, lam tron den 2 chu so thap phan.
    Service chua kiem tra cac gioi han nay; khong doi Java trong pham vi nay.
  - Khong chen du lieu mau, khong tao bang cua Dich vu / Phan hoi.
*/

USE master;
GO

IF DB_ID(N'QLHDKhachSan') IS NULL
BEGIN
    CREATE DATABASE QLHDKhachSan;
END;
GO

USE QLHDKhachSan;
GO

-- 1. HOA DON
IF OBJECT_ID(N'dbo.HoaDon', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.HoaDon
    (
        MaHoaDon       VARCHAR(30)    NOT NULL,
        MaDatPhong     VARCHAR(30)    NOT NULL,
        NgayLap        DATE           NOT NULL CONSTRAINT DF_HoaDon_NgayLap DEFAULT (CONVERT(date, GETDATE())),
        TienGiam       DECIMAL(18,2)  NOT NULL CONSTRAINT DF_HoaDon_TienGiam DEFAULT (0),
        TongTien       DECIMAL(18,2)  NOT NULL CONSTRAINT DF_HoaDon_TongTien DEFAULT (0),
        TrangThai      NVARCHAR(50)   NOT NULL CONSTRAINT DF_HoaDon_TrangThai DEFAULT (N'Chưa thanh toán'),
        CONSTRAINT PK_HoaDon PRIMARY KEY (MaHoaDon),
        CONSTRAINT CK_HoaDon_TienGiam CHECK (TienGiam >= 0),
        CONSTRAINT CK_HoaDon_TongTien CHECK (TongTien >= 0),
        CONSTRAINT CK_HoaDon_ThongTin CHECK (
            LEN(LTRIM(RTRIM(MaHoaDon))) > 0 AND
            LEN(LTRIM(RTRIM(MaDatPhong))) > 0 AND
            LEN(LTRIM(RTRIM(TrangThai))) > 0)
    );
END;
GO

-- 2. CHI TIET HOA DON
-- ThanhTien la cot thuong, vi code Java hien tai co logic tinh va cap nhat tien.
-- DAO truyen ThanhTien trong ca INSERT va UPDATE: khong dung computed/trigger.
IF OBJECT_ID(N'dbo.ChiTietHoaDon', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ChiTietHoaDon
    (
        MaChiTiet       VARCHAR(30)    NOT NULL,
        MaHoaDon        VARCHAR(30)    NOT NULL,
        MoTa           NVARCHAR(500)  NOT NULL,
        LoaiChiPhi      NVARCHAR(50)   NOT NULL,
        MaPhong         VARCHAR(30)    NULL,
        MaDichVu        VARCHAR(30)    NULL,
        SoLuong         INT            NOT NULL,
        DonGia          DECIMAL(18,2)  NOT NULL,
        ThanhTien       DECIMAL(18,2)  NOT NULL,
        CONSTRAINT PK_ChiTietHoaDon PRIMARY KEY (MaChiTiet),
        CONSTRAINT FK_ChiTietHoaDon_HoaDon FOREIGN KEY (MaHoaDon)
            REFERENCES dbo.HoaDon (MaHoaDon),
        CONSTRAINT CK_ChiTietHoaDon_SoLuong CHECK (SoLuong > 0),
        CONSTRAINT CK_ChiTietHoaDon_DonGia CHECK (DonGia >= 0),
        CONSTRAINT CK_ChiTietHoaDon_ThanhTien CHECK (ThanhTien >= 0),
        CONSTRAINT CK_ChiTietHoaDon_ThongTin CHECK (
            LEN(LTRIM(RTRIM(LoaiChiPhi))) > 0 AND
            LEN(LTRIM(RTRIM(MoTa))) > 0)
    );
END;
GO

-- 3. THANH TOAN
-- LocalDate / ResultSet.getDate(): chi luu ngay, khong luu gio.
IF OBJECT_ID(N'dbo.ThanhToan', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ThanhToan
    (
        MaThanhToan      VARCHAR(30)    NOT NULL,
        MaHoaDon         VARCHAR(30)    NOT NULL,
        NgayThanhToan    DATE           NOT NULL CONSTRAINT DF_ThanhToan_Ngay DEFAULT (CONVERT(date, GETDATE())),
        SoTien           DECIMAL(18,2)  NOT NULL,
        PhuongThuc       NVARCHAR(50)   NOT NULL,
        TrangThai        NVARCHAR(50)   NOT NULL,
        CONSTRAINT PK_ThanhToan PRIMARY KEY (MaThanhToan),
        CONSTRAINT FK_ThanhToan_HoaDon FOREIGN KEY (MaHoaDon)
            REFERENCES dbo.HoaDon (MaHoaDon),
        CONSTRAINT CK_ThanhToan_SoTien CHECK (SoTien > 0),
        CONSTRAINT CK_ThanhToan_ThongTin CHECK (
            LEN(LTRIM(RTRIM(MaThanhToan))) > 0 AND
            LEN(LTRIM(RTRIM(PhuongThuc))) > 0 AND
            LEN(LTRIM(RTRIM(TrangThai))) > 0)
    );
END;
GO

-- 4. INDEX PHUC VU TRA CUU VA THONG KE
IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = N'IX_HoaDon_NgayLap' AND object_id = OBJECT_ID(N'dbo.HoaDon')
)
    CREATE INDEX IX_HoaDon_NgayLap ON dbo.HoaDon (NgayLap);
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = N'IX_HoaDon_MaDatPhong' AND object_id = OBJECT_ID(N'dbo.HoaDon')
)
    CREATE INDEX IX_HoaDon_MaDatPhong ON dbo.HoaDon (MaDatPhong);
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = N'IX_ChiTietHoaDon_MaHoaDon' AND object_id = OBJECT_ID(N'dbo.ChiTietHoaDon')
)
    CREATE INDEX IX_ChiTietHoaDon_MaHoaDon ON dbo.ChiTietHoaDon (MaHoaDon);
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = N'IX_ThanhToan_MaHoaDon' AND object_id = OBJECT_ID(N'dbo.ThanhToan')
)
    CREATE INDEX IX_ThanhToan_MaHoaDon ON dbo.ThanhToan (MaHoaDon);
GO

IF NOT EXISTS (
    SELECT 1 FROM sys.indexes
    WHERE name = N'IX_ThanhToan_Ngay' AND object_id = OBJECT_ID(N'dbo.ThanhToan')
)
    CREATE INDEX IX_ThanhToan_Ngay ON dbo.ThanhToan (NgayThanhToan);
GO

-- 5. KIEM TRA CAU TRUC (CHI DOC, KHONG SUA DU LIEU)
SELECT TABLE_NAME, COLUMN_NAME, DATA_TYPE, CHARACTER_MAXIMUM_LENGTH,
       NUMERIC_PRECISION, NUMERIC_SCALE, IS_NULLABLE
FROM INFORMATION_SCHEMA.COLUMNS
WHERE TABLE_SCHEMA = 'dbo'
  AND TABLE_NAME IN ('HoaDon', 'ChiTietHoaDon', 'ThanhToan')
ORDER BY TABLE_NAME, ORDINAL_POSITION;
GO

/*
  THONG KE TAI CHINH:
  Khong can bang ThongKeTaiChinh / ThongKeTheoNgay, vi day la ket qua
  truy van tong hop tu HoaDon va ThanhToan. Khong tu dong tao VIEW
  de tranh lech SQL hien tai cua ThongKeTaiChinhDAO.

  CAC QUY TAC DA DOI CHIEU:
  - PK la chuoi do Java truyen khi INSERT, khong IDENTITY.
  - MaPhong / MaDichVu cho phep NULL dung voi DAO.setNull(Types.VARCHAR).
  - Hai FK noi bo khong CASCADE: HoaDonDAO.delete da xoa ThanhToan,
    ChiTietHoaDon truoc HoaDon trong cung transaction.
  - Khong UNIQUE tren MaHoaDon cua bang con: mot hoa don co nhieu chi tiet
    va nhieu lan thanh toan. Khong UNIQUE tren HoaDon.MaDatPhong khi chua
    co quy tac tu Service.
  - TienGiam / TongTien / DonGia / ThanhTien >= 0; SoLuong / SoTien > 0.
    Khong CHECK TienGiam <= TongTien: hoa don moi co TongTien = 0.
    Khong trigger tinh tien hay dong bo trang thai; Java phu trach viec nay.
  - TrangThai, PhuongThuc, LoaiChiPhi la NVARCHAR de giu tieng Viet.
    Service chi bat buoc khong rong, khong khoa danh sach bang CHECK IN.
    Thong ke chi cong ThanhToan.TrangThai IN (N'Thành công', N'Đã thanh toán').
    Cac trang thai khac khong duoc cong vao thuc thu.
  - Thong ke loc va GROUP BY HoaDon.NgayLap (DATE), KHONG theo ngay thu tien.
    Doanh thu lay TongTien da luu; thuc thu gioi han toi TongTien;
    con no khong am. Khong tao cot rieng cho cac alias ket qua SUM / COUNT.
*/

-- 6. CHAN DOAN PHU THUOC VA SCHEMA CU (chi doc metadata).
-- Danh sach nay phai rong sau khi tao moi ba bang Tai chinh.
SELECT expected.TenBang, expected.TenCot AS CotDAOCanNhungDangThieu
FROM (VALUES
    (N'HoaDon', N'MaHoaDon'), (N'HoaDon', N'MaDatPhong'),
    (N'HoaDon', N'NgayLap'), (N'HoaDon', N'TienGiam'),
    (N'HoaDon', N'TongTien'), (N'HoaDon', N'TrangThai'),
    (N'ChiTietHoaDon', N'MaChiTiet'), (N'ChiTietHoaDon', N'MaHoaDon'),
    (N'ChiTietHoaDon', N'MaPhong'), (N'ChiTietHoaDon', N'MaDichVu'),
    (N'ChiTietHoaDon', N'LoaiChiPhi'), (N'ChiTietHoaDon', N'MoTa'),
    (N'ChiTietHoaDon', N'SoLuong'), (N'ChiTietHoaDon', N'DonGia'),
    (N'ChiTietHoaDon', N'ThanhTien'),
    (N'ThanhToan', N'MaThanhToan'), (N'ThanhToan', N'MaHoaDon'),
    (N'ThanhToan', N'NgayThanhToan'), (N'ThanhToan', N'PhuongThuc'),
    (N'ThanhToan', N'SoTien'), (N'ThanhToan', N'TrangThai')
) AS expected(TenBang, TenCot)
WHERE COL_LENGTH(N'dbo.' + expected.TenBang, expected.TenCot) IS NULL;

IF OBJECT_ID(N'dbo.DatPhong', N'U') IS NULL
   OR COL_LENGTH(N'dbo.DatPhong', N'MaDatPhong') IS NULL
   OR COL_LENGTH(N'dbo.DatPhong', N'MaKhachHang') IS NULL
BEGIN
    PRINT N'CHUA DU PHU THUOC: HoaDonDAO.search can dbo.DatPhong(MaDatPhong, MaKhachHang).';
    PRINT N'Can script chinh thuc cua module Dat phong; khong tu tao bang trong script Tai chinh.';
END;
GO
