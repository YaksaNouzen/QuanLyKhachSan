/*
    QUAN LY KHACH SAN - MASTER DATABASE
    SQL Server 2022+

    - Database chung: QuanLyKhachSan
    - 31 bang cho 15 module
    - Khong chen du lieu mau, khong DROP/DELETE du lieu
    - Ma chuoi duoc cap qua dbo.sp_LayMaTuDong
    - Ma so nguyen dung IDENTITY

    CHU Y: File nay chi dung de tao database moi. Neu schema da ton tai,
    script se dung lai de bao ve du lieu; hay dung database/patches cho DB cu.
*/

USE master;
GO

IF DB_ID(N'QuanLyKhachSan') IS NULL
    EXEC(N'CREATE DATABASE QuanLyKhachSan');
GO

USE QuanLyKhachSan;
GO

IF DB_NAME() <> N'QuanLyKhachSan'
    THROW 51000, N'Sai database dich. Khong tao schema.', 1;
GO

IF OBJECT_ID(N'dbo.KhachHang', N'U') IS NOT NULL
   OR OBJECT_ID(N'dbo.DatPhong', N'U') IS NOT NULL
   OR OBJECT_ID(N'dbo.HoaDon', N'U') IS NOT NULL
BEGIN
    THROW 51001,
        N'FULL_DATABASE.sql chi dung cho database moi. Schema da ton tai; hay chay patch moi.',
        1;
END;
GO

SET ANSI_NULLS ON;
SET QUOTED_IDENTIFIER ON;
SET XACT_ABORT ON;
GO

/* ========================================================================
   BO DEM MA TU DONG
   ======================================================================== */
CREATE SEQUENCE dbo.SeqKhachHang      AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;
CREATE SEQUENCE dbo.SeqDatPhong       AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;
CREATE SEQUENCE dbo.SeqLuotLuuTru     AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;
CREATE SEQUENCE dbo.SeqYeuCauDatPhong AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;
CREATE SEQUENCE dbo.SeqChuyenPhong    AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;
CREATE SEQUENCE dbo.SeqPhong          AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;
CREATE SEQUENCE dbo.SeqLoaiPhong      AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;
CREATE SEQUENCE dbo.SeqTang           AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;
CREATE SEQUENCE dbo.SeqTienNghi       AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;
CREATE SEQUENCE dbo.SeqVeSinh         AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;
CREATE SEQUENCE dbo.SeqBaoTri         AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;
CREATE SEQUENCE dbo.SeqSuDungDichVu   AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;
CREATE SEQUENCE dbo.SeqNhanVien       AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;
CREATE SEQUENCE dbo.SeqCaLam          AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;
CREATE SEQUENCE dbo.SeqVatTu          AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;
CREATE SEQUENCE dbo.SeqNhaCungCap     AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;
CREATE SEQUENCE dbo.SeqPhieuNhap      AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;
CREATE SEQUENCE dbo.SeqHoaDon         AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;
CREATE SEQUENCE dbo.SeqChiTietHoaDon  AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;
CREATE SEQUENCE dbo.SeqThanhToan      AS BIGINT START WITH 1 INCREMENT BY 1 NO CYCLE CACHE 20;
GO

CREATE PROCEDURE dbo.sp_LayMaTuDong
    @LoaiMa VARCHAR(30)
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @So BIGINT;
    DECLARE @TienTo VARCHAR(10);
    DECLARE @DoDai INT = 6;
    SET @LoaiMa = UPPER(TRIM(@LoaiMa));

    IF @LoaiMa = 'KHACH_HANG'
        SELECT @So = NEXT VALUE FOR dbo.SeqKhachHang, @TienTo = 'KH';
    ELSE IF @LoaiMa = 'DAT_PHONG'
        SELECT @So = NEXT VALUE FOR dbo.SeqDatPhong, @TienTo = 'DP';
    ELSE IF @LoaiMa = 'LUOT_LUU_TRU'
        SELECT @So = NEXT VALUE FOR dbo.SeqLuotLuuTru, @TienTo = 'LT';
    ELSE IF @LoaiMa = 'YEU_CAU_DAT_PHONG'
        SELECT @So = NEXT VALUE FOR dbo.SeqYeuCauDatPhong, @TienTo = 'YC';
    ELSE IF @LoaiMa = 'CHUYEN_PHONG'
        SELECT @So = NEXT VALUE FOR dbo.SeqChuyenPhong, @TienTo = 'CP';
    ELSE IF @LoaiMa = 'PHONG'
        SELECT @So = NEXT VALUE FOR dbo.SeqPhong, @TienTo = 'P';
    ELSE IF @LoaiMa = 'LOAI_PHONG'
        SELECT @So = NEXT VALUE FOR dbo.SeqLoaiPhong, @TienTo = 'LP';
    ELSE IF @LoaiMa = 'TANG'
        SELECT @So = NEXT VALUE FOR dbo.SeqTang, @TienTo = 'T';
    ELSE IF @LoaiMa = 'TIEN_NGHI'
        SELECT @So = NEXT VALUE FOR dbo.SeqTienNghi, @TienTo = 'TN';
    ELSE IF @LoaiMa = 'VE_SINH'
        SELECT @So = NEXT VALUE FOR dbo.SeqVeSinh, @TienTo = 'VS';
    ELSE IF @LoaiMa = 'BAO_TRI'
        SELECT @So = NEXT VALUE FOR dbo.SeqBaoTri, @TienTo = 'BT';
    ELSE IF @LoaiMa = 'SU_DUNG_DICH_VU'
        SELECT @So = NEXT VALUE FOR dbo.SeqSuDungDichVu, @TienTo = 'SD';
    ELSE IF @LoaiMa = 'NHAN_VIEN'
        SELECT @So = NEXT VALUE FOR dbo.SeqNhanVien, @TienTo = 'NV';
    ELSE IF @LoaiMa = 'CA_LAM'
        SELECT @So = NEXT VALUE FOR dbo.SeqCaLam, @TienTo = 'CA';
    ELSE IF @LoaiMa = 'VAT_TU'
        SELECT @So = NEXT VALUE FOR dbo.SeqVatTu, @TienTo = 'VT';
    ELSE IF @LoaiMa = 'NHA_CUNG_CAP'
        SELECT @So = NEXT VALUE FOR dbo.SeqNhaCungCap, @TienTo = 'NCC';
    ELSE IF @LoaiMa = 'PHIEU_NHAP'
        SELECT @So = NEXT VALUE FOR dbo.SeqPhieuNhap, @TienTo = 'PN';
    ELSE IF @LoaiMa = 'HOA_DON'
        SELECT @So = NEXT VALUE FOR dbo.SeqHoaDon, @TienTo = 'HD';
    ELSE IF @LoaiMa = 'CHI_TIET_HOA_DON'
        SELECT @So = NEXT VALUE FOR dbo.SeqChiTietHoaDon, @TienTo = 'CT';
    ELSE IF @LoaiMa = 'THANH_TOAN'
        SELECT @So = NEXT VALUE FOR dbo.SeqThanhToan, @TienTo = 'TT';
    ELSE
        THROW 51002, N'Loai ma tu dong khong duoc ho tro.', 1;

    IF LEN(CONVERT(VARCHAR(30), @So)) > @DoDai
        THROW 51003, N'Bo dem ma da vuot qua do dai cau hinh.', 1;

    SELECT CONCAT(@TienTo,
                  RIGHT(REPLICATE('0', @DoDai) + CONVERT(VARCHAR(30), @So), @DoDai)) AS MaMoi;
END;
GO

/* ========================================================================
   NHOM 1 + NHOM 2: LE TAN, LUU TRU, PHONG, BUONG PHONG, BAO TRI
   ======================================================================== */
CREATE TABLE dbo.KhachHang (
    MaKhachHang VARCHAR(20) NOT NULL CONSTRAINT PK_KhachHang PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    LoaiGiayTo VARCHAR(10) NULL,
    SoGiayTo VARCHAR(30) NULL,
    QuocGiaCap CHAR(2) NULL,
    SoDienThoai VARCHAR(20) NULL,
    Email NVARCHAR(254) NULL,
    NgaySinh DATE NULL,
    GioiTinh VARCHAR(15) NOT NULL CONSTRAINT DF_KhachHang_GioiTinh DEFAULT 'KHONG_CUNG_CAP',
    QuocTich CHAR(2) NULL,
    DiaChi NVARCHAR(255) NULL,
    NgayTao DATETIME2(0) NOT NULL CONSTRAINT DF_KhachHang_NgayTao DEFAULT SYSDATETIME(),
    TrangThai VARCHAR(20) NOT NULL CONSTRAINT DF_KhachHang_TrangThai DEFAULT 'HOAT_DONG'
);
GO
CREATE UNIQUE INDEX UX_KhachHang_GiayTo
    ON dbo.KhachHang(LoaiGiayTo, QuocGiaCap, SoGiayTo)
    WHERE SoGiayTo IS NOT NULL;
GO

CREATE TABLE dbo.Tang (
    MaTang VARCHAR(20) NOT NULL CONSTRAINT PK_Tang PRIMARY KEY,
    TenTang NVARCHAR(100) NOT NULL,
    CONSTRAINT UQ_Tang_TenTang UNIQUE (TenTang)
);

CREATE TABLE dbo.LoaiPhong (
    MaLoaiPhong VARCHAR(20) NOT NULL CONSTRAINT PK_LoaiPhong PRIMARY KEY,
    TenLoai NVARCHAR(100) NOT NULL,
    GiaTheoNgay DECIMAL(18,2) NOT NULL,
    SoNguoiToiDa INT NOT NULL,
    CONSTRAINT UQ_LoaiPhong_TenLoai UNIQUE (TenLoai),
    CONSTRAINT CK_LoaiPhong_Gia CHECK (GiaTheoNgay >= 0),
    CONSTRAINT CK_LoaiPhong_SoNguoi CHECK (SoNguoiToiDa > 0)
);

CREATE TABLE dbo.Phong (
    MaPhong VARCHAR(20) NOT NULL CONSTRAINT PK_Phong PRIMARY KEY,
    SoPhong NVARCHAR(20) NOT NULL,
    MaLoaiPhong VARCHAR(20) NOT NULL,
    MaTang VARCHAR(20) NOT NULL,
    GiaPhong DECIMAL(18,2) NULL,
    TrangThai VARCHAR(20) NOT NULL CONSTRAINT DF_Phong_TrangThai DEFAULT 'TRONG',
    NgungSuDung BIT NOT NULL CONSTRAINT DF_Phong_NgungSuDung DEFAULT 0,
    CONSTRAINT UQ_Phong_SoPhong UNIQUE (SoPhong),
    CONSTRAINT FK_Phong_LoaiPhong FOREIGN KEY (MaLoaiPhong) REFERENCES dbo.LoaiPhong(MaLoaiPhong),
    CONSTRAINT FK_Phong_Tang FOREIGN KEY (MaTang) REFERENCES dbo.Tang(MaTang),
    CONSTRAINT CK_Phong_Gia CHECK (GiaPhong IS NULL OR GiaPhong >= 0)
);

CREATE TABLE dbo.ChiTietPhong (
    MaPhong VARCHAR(20) NOT NULL CONSTRAINT PK_ChiTietPhong PRIMARY KEY,
    MoTa NVARCHAR(2000) NOT NULL CONSTRAINT DF_ChiTietPhong_MoTa DEFAULT N'',
    CONSTRAINT FK_ChiTietPhong_Phong FOREIGN KEY (MaPhong) REFERENCES dbo.Phong(MaPhong)
);

CREATE TABLE dbo.PhongTienNghi (
    MaPhong VARCHAR(20) NOT NULL,
    MaTienNghi VARCHAR(20) NOT NULL,
    TenTienNghi NVARCHAR(100) NOT NULL,
    SoLuong INT NOT NULL CONSTRAINT DF_PhongTienNghi_SoLuong DEFAULT 1,
    TinhTrang VARCHAR(20) NOT NULL CONSTRAINT DF_PhongTienNghi_TinhTrang DEFAULT 'TOT',
    CONSTRAINT PK_PhongTienNghi PRIMARY KEY (MaPhong, MaTienNghi),
    CONSTRAINT FK_PhongTienNghi_Phong FOREIGN KEY (MaPhong) REFERENCES dbo.Phong(MaPhong),
    CONSTRAINT CK_PhongTienNghi_SoLuong CHECK (SoLuong > 0)
);

CREATE TABLE dbo.LuotLuuTru (
    MaLuotLuuTru VARCHAR(20) NOT NULL CONSTRAINT PK_LuotLuuTru PRIMARY KEY,
    MaKhachHang VARCHAR(20) NOT NULL,
    TaoLuc DATETIME2(0) NOT NULL CONSTRAINT DF_LuotLuuTru_TaoLuc DEFAULT SYSDATETIME(),
    CONSTRAINT FK_LuotLuuTru_KhachHang FOREIGN KEY (MaKhachHang) REFERENCES dbo.KhachHang(MaKhachHang)
);

CREATE TABLE dbo.DatPhong (
    MaDatPhong VARCHAR(20) NOT NULL CONSTRAINT PK_DatPhong PRIMARY KEY,
    MaKhachHang VARCHAR(20) NOT NULL,
    MaPhong VARCHAR(20) NOT NULL,
    GiaPhongChot DECIMAL(18,2) NOT NULL,
    NgayDat DATETIME2(0) NOT NULL CONSTRAINT DF_DatPhong_NgayDat DEFAULT SYSDATETIME(),
    NgayNhan DATETIME2(0) NULL,
    NgayTra DATETIME2(0) NULL,
    TrangThai VARCHAR(20) NOT NULL CONSTRAINT DF_DatPhong_TrangThai DEFAULT 'GIU_PHONG',
    MaLuotLuuTru VARCHAR(20) NULL,
    NgayNhanDuKien DATETIME2(0) NOT NULL,
    NgayTraDuKien DATETIME2(0) NOT NULL,
    CONSTRAINT FK_DatPhong_KhachHang FOREIGN KEY (MaKhachHang) REFERENCES dbo.KhachHang(MaKhachHang),
    CONSTRAINT FK_DatPhong_Phong FOREIGN KEY (MaPhong) REFERENCES dbo.Phong(MaPhong),
    CONSTRAINT FK_DatPhong_LuotLuuTru FOREIGN KEY (MaLuotLuuTru) REFERENCES dbo.LuotLuuTru(MaLuotLuuTru),
    CONSTRAINT CK_DatPhong_Gia CHECK (GiaPhongChot >= 0),
    CONSTRAINT CK_DatPhong_ThoiGian CHECK (NgayTraDuKien > NgayNhanDuKien)
);

CREATE TABLE dbo.MaDatPhongDaCap (
    MaDatPhong VARCHAR(20) NOT NULL CONSTRAINT PK_MaDatPhongDaCap PRIMARY KEY,
    ThoiGianDangKy DATETIME2(0) NOT NULL CONSTRAINT DF_MaDatPhongDaCap_ThoiGian DEFAULT SYSDATETIME()
);

CREATE TABLE dbo.YeuCauDatPhong (
    MaYeuCau VARCHAR(20) NOT NULL CONSTRAINT PK_YeuCauDatPhong PRIMARY KEY,
    MaKhachHang VARCHAR(20) NOT NULL,
    MaPhong VARCHAR(20) NOT NULL,
    GhiChu NVARCHAR(1000) NOT NULL CONSTRAINT DF_YeuCauDatPhong_GhiChu DEFAULT N'',
    TrangThai VARCHAR(20) NOT NULL CONSTRAINT DF_YeuCauDatPhong_TrangThai DEFAULT 'CHO_XU_LY',
    MaDatPhong VARCHAR(20) NULL,
    LyDo NVARCHAR(1000) NOT NULL CONSTRAINT DF_YeuCauDatPhong_LyDo DEFAULT N'',
    PhienBan BIGINT NOT NULL CONSTRAINT DF_YeuCauDatPhong_PhienBan DEFAULT 0,
    TaoLuc DATETIME2(0) NOT NULL CONSTRAINT DF_YeuCauDatPhong_TaoLuc DEFAULT SYSDATETIME(),
    CapNhatLuc DATETIME2(0) NOT NULL CONSTRAINT DF_YeuCauDatPhong_CapNhatLuc DEFAULT SYSDATETIME(),
    CONSTRAINT FK_YeuCauDatPhong_KhachHang FOREIGN KEY (MaKhachHang) REFERENCES dbo.KhachHang(MaKhachHang),
    CONSTRAINT FK_YeuCauDatPhong_Phong FOREIGN KEY (MaPhong) REFERENCES dbo.Phong(MaPhong),
    CONSTRAINT FK_YeuCauDatPhong_DatPhong FOREIGN KEY (MaDatPhong) REFERENCES dbo.DatPhong(MaDatPhong)
);

CREATE TABLE dbo.ChuyenPhong (
    MaChuyenPhong VARCHAR(20) NOT NULL CONSTRAINT PK_ChuyenPhong PRIMARY KEY,
    MaDatPhongCu VARCHAR(20) NOT NULL,
    MaDatPhongMoi VARCHAR(20) NOT NULL,
    LyDo NVARCHAR(1000) NOT NULL,
    NguoiThucHien NVARCHAR(100) NULL,
    ThoiGian DATETIME2(0) NOT NULL CONSTRAINT DF_ChuyenPhong_ThoiGian DEFAULT SYSDATETIME(),
    CONSTRAINT FK_ChuyenPhong_DatPhongCu FOREIGN KEY (MaDatPhongCu) REFERENCES dbo.DatPhong(MaDatPhong),
    CONSTRAINT FK_ChuyenPhong_DatPhongMoi FOREIGN KEY (MaDatPhongMoi) REFERENCES dbo.DatPhong(MaDatPhong),
    CONSTRAINT CK_ChuyenPhong_KhacDon CHECK (MaDatPhongCu <> MaDatPhongMoi)
);

CREATE TABLE dbo.VeSinh (
    MaVeSinh VARCHAR(36) NOT NULL CONSTRAINT PK_VeSinh PRIMARY KEY,
    MaPhong VARCHAR(20) NOT NULL,
    NoiDung NVARCHAR(500) NOT NULL,
    NguoiPhuTrach NVARCHAR(100) NOT NULL,
    BatDau DATETIME2(0) NULL,
    KetThuc DATETIME2(0) NULL,
    TrangThai VARCHAR(20) NOT NULL CONSTRAINT DF_VeSinh_TrangThai DEFAULT 'CHO_DON',
    NgayPhanCong DATETIME2(0) NOT NULL CONSTRAINT DF_VeSinh_NgayPhanCong DEFAULT SYSDATETIME(),
    NguoiXacNhan NVARCHAR(100) NULL,
    XacNhanLuc DATETIME2(0) NULL,
    CONSTRAINT FK_VeSinh_Phong FOREIGN KEY (MaPhong) REFERENCES dbo.Phong(MaPhong)
);

CREATE TABLE dbo.BaoTri (
    MaBaoTri VARCHAR(36) NOT NULL CONSTRAINT PK_BaoTri PRIMARY KEY,
    MaPhong VARCHAR(20) NOT NULL,
    NoiDung NVARCHAR(500) NOT NULL,
    NguoiPhuTrach NVARCHAR(100) NULL,
    BatDau DATETIME2(0) NULL,
    KetThuc DATETIME2(0) NULL,
    TrangThai VARCHAR(20) NOT NULL CONSTRAINT DF_BaoTri_TrangThai DEFAULT 'BAO_HONG',
    MucDo VARCHAR(20) NOT NULL CONSTRAINT DF_BaoTri_MucDo DEFAULT 'TRUNG_BINH',
    NguoiBao NVARCHAR(100) NOT NULL CONSTRAINT DF_BaoTri_NguoiBao DEFAULT N'Chưa rõ',
    NgayBao DATETIME2(0) NOT NULL CONSTRAINT DF_BaoTri_NgayBao DEFAULT SYSDATETIME(),
    ChiPhi DECIMAL(18,2) NOT NULL CONSTRAINT DF_BaoTri_ChiPhi DEFAULT 0,
    KetQua NVARCHAR(500) NOT NULL CONSTRAINT DF_BaoTri_KetQua DEFAULT N'',
    CONSTRAINT FK_BaoTri_Phong FOREIGN KEY (MaPhong) REFERENCES dbo.Phong(MaPhong),
    CONSTRAINT CK_BaoTri_ChiPhi CHECK (ChiPhi >= 0)
);

CREATE TABLE dbo.PhongLuuTruNhan (
    MaDatPhong VARCHAR(20) NOT NULL CONSTRAINT PK_PhongLuuTruNhan PRIMARY KEY,
    MaPhong VARCHAR(20) NOT NULL,
    MaKhachHang VARCHAR(20) NOT NULL,
    TrangThai VARCHAR(20) NOT NULL,
    PhienBan BIGINT NOT NULL CONSTRAINT DF_PhongLuuTruNhan_PhienBan DEFAULT 1,
    CapNhatLuc DATETIME2(0) NOT NULL CONSTRAINT DF_PhongLuuTruNhan_CapNhatLuc DEFAULT SYSDATETIME(),
    CONSTRAINT FK_PhongLuuTruNhan_DatPhong FOREIGN KEY (MaDatPhong) REFERENCES dbo.DatPhong(MaDatPhong),
    CONSTRAINT FK_PhongLuuTruNhan_Phong FOREIGN KEY (MaPhong) REFERENCES dbo.Phong(MaPhong),
    CONSTRAINT FK_PhongLuuTruNhan_KhachHang FOREIGN KEY (MaKhachHang) REFERENCES dbo.KhachHang(MaKhachHang)
);
GO

/* ========================================================================
   NHOM 5 - DICH VU CHUAN (thay bang DichVu cu cua nhom 1)
   ======================================================================== */
CREATE TABLE dbo.LoaiDichVu (
    MaLoaiDV INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_LoaiDichVu PRIMARY KEY,
    MaLoaiDVHienThi AS ('LDV' + RIGHT('000000' + CONVERT(VARCHAR(6), MaLoaiDV), 6)) PERSISTED,
    TenLoaiDV NVARCHAR(100) NOT NULL,
    MoTa NVARCHAR(255) NULL,
    TrangThai BIT NOT NULL CONSTRAINT DF_LoaiDichVu_TrangThai DEFAULT 1,
    CONSTRAINT UQ_LoaiDichVu_Ten UNIQUE (TenLoaiDV),
    CONSTRAINT UQ_LoaiDichVu_MaHienThi UNIQUE (MaLoaiDVHienThi)
);

CREATE TABLE dbo.DichVu (
    MaDV INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_DichVu PRIMARY KEY,
    MaDVHienThi AS ('DV' + RIGHT('000000' + CONVERT(VARCHAR(6), MaDV), 6)) PERSISTED,
    MaLoaiDV INT NOT NULL,
    TenDV NVARCHAR(150) NOT NULL,
    DonGia DECIMAL(18,2) NOT NULL,
    DonViTinh NVARCHAR(30) NULL,
    MoTa NVARCHAR(255) NULL,
    TrangThai BIT NOT NULL CONSTRAINT DF_DichVu_TrangThai DEFAULT 1,
    CONSTRAINT FK_DichVu_LoaiDichVu FOREIGN KEY (MaLoaiDV) REFERENCES dbo.LoaiDichVu(MaLoaiDV),
    CONSTRAINT UQ_DichVu_Loai_Ten UNIQUE (MaLoaiDV, TenDV),
    CONSTRAINT UQ_DichVu_MaHienThi UNIQUE (MaDVHienThi),
    CONSTRAINT CK_DichVu_DonGia CHECK (DonGia >= 0)
);

CREATE TABLE dbo.SuDungDichVu (
    MaSuDung VARCHAR(36) NOT NULL CONSTRAINT PK_SuDungDichVu PRIMARY KEY,
    MaDatPhong VARCHAR(20) NOT NULL,
    MaDichVu INT NOT NULL,
    SoLuong INT NOT NULL,
    DonGia DECIMAL(18,2) NOT NULL,
    ThoiGianSuDung DATETIME2(0) NOT NULL CONSTRAINT DF_SuDungDichVu_ThoiGian DEFAULT SYSDATETIME(),
    ThanhTien AS (CONVERT(DECIMAL(18,2), SoLuong * DonGia)) PERSISTED,
    CONSTRAINT FK_SuDungDichVu_DatPhong FOREIGN KEY (MaDatPhong) REFERENCES dbo.DatPhong(MaDatPhong),
    CONSTRAINT FK_SuDungDichVu_DichVu FOREIGN KEY (MaDichVu) REFERENCES dbo.DichVu(MaDV),
    CONSTRAINT CK_SuDungDichVu_SoLuong CHECK (SoLuong > 0),
    CONSTRAINT CK_SuDungDichVu_DonGia CHECK (DonGia >= 0)
);
GO

/* ========================================================================
   NHOM 3: NHAN SU VA HE THONG
   ======================================================================== */
CREATE TABLE dbo.VaiTro (
    MaVaiTro VARCHAR(10) NOT NULL CONSTRAINT PK_VaiTro PRIMARY KEY,
    TenVaiTro NVARCHAR(50) NOT NULL,
    CONSTRAINT UQ_VaiTro_Ten UNIQUE (TenVaiTro)
);

CREATE TABLE dbo.NhanVien (
    MaNV VARCHAR(10) NOT NULL CONSTRAINT PK_NhanVien PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    SoGiayTo VARCHAR(30) NULL,
    GioiTinh NVARCHAR(5) NULL,
    NgaySinh DATE NULL,
    SDT VARCHAR(15) NULL,
    Email VARCHAR(100) NULL,
    DiaChi NVARCHAR(200) NULL,
    BoPhan NVARCHAR(50) NULL,
    ChucVu NVARCHAR(50) NULL,
    NgayVaoLam DATE NULL,
    TrangThai NVARCHAR(30) NOT NULL CONSTRAINT DF_NhanVien_TrangThai DEFAULT N'Đang làm',
    CONSTRAINT CK_NhanVien_TrangThai CHECK (TrangThai IN (N'Đang làm', N'Nghỉ phép', N'Đã nghỉ việc'))
);
GO
CREATE UNIQUE INDEX UX_NhanVien_SoGiayTo ON dbo.NhanVien(SoGiayTo) WHERE SoGiayTo IS NOT NULL;
CREATE UNIQUE INDEX UX_NhanVien_Email ON dbo.NhanVien(Email) WHERE Email IS NOT NULL;
GO

CREATE TABLE dbo.TaiKhoan (
    TenDangNhap VARCHAR(50) NOT NULL CONSTRAINT PK_TaiKhoan PRIMARY KEY,
    MatKhau VARCHAR(64) NOT NULL,
    MaNV VARCHAR(10) NOT NULL,
    MaVaiTro VARCHAR(10) NOT NULL,
    TrangThai BIT NOT NULL CONSTRAINT DF_TaiKhoan_TrangThai DEFAULT 1,
    CONSTRAINT UQ_TaiKhoan_MaNV UNIQUE (MaNV),
    CONSTRAINT FK_TaiKhoan_NhanVien FOREIGN KEY (MaNV) REFERENCES dbo.NhanVien(MaNV),
    CONSTRAINT FK_TaiKhoan_VaiTro FOREIGN KEY (MaVaiTro) REFERENCES dbo.VaiTro(MaVaiTro)
);

CREATE TABLE dbo.CaLam (
    MaCa VARCHAR(10) NOT NULL CONSTRAINT PK_CaLam PRIMARY KEY,
    TenCa NVARCHAR(50) NOT NULL,
    GioBatDau TIME NOT NULL,
    GioKetThuc TIME NOT NULL,
    CONSTRAINT UQ_CaLam_Ten UNIQUE (TenCa),
    CONSTRAINT UQ_CaLam_ThoiGian UNIQUE (GioBatDau, GioKetThuc)
);

CREATE TABLE dbo.PhanCa (
    MaPhanCa INT IDENTITY(1,1) NOT NULL CONSTRAINT PK_PhanCa PRIMARY KEY,
    MaNV VARCHAR(10) NOT NULL,
    MaCa VARCHAR(10) NOT NULL,
    NgayLam DATE NOT NULL,
    TrangThai NVARCHAR(30) NOT NULL CONSTRAINT DF_PhanCa_TrangThai DEFAULT N'Chưa bàn giao',
    NoiDungBanGiao NVARCHAR(500) NULL,
    CONSTRAINT FK_PhanCa_NhanVien FOREIGN KEY (MaNV) REFERENCES dbo.NhanVien(MaNV),
    CONSTRAINT FK_PhanCa_CaLam FOREIGN KEY (MaCa) REFERENCES dbo.CaLam(MaCa),
    CONSTRAINT UQ_PhanCa_NhanVien_Ca_Ngay UNIQUE (MaNV, MaCa, NgayLam)
);
GO

/* ========================================================================
   NHOM 4: KHO VA CUNG UNG
   ======================================================================== */
CREATE TABLE dbo.VatTu (
    MaVatTu VARCHAR(20) NOT NULL CONSTRAINT PK_VatTu PRIMARY KEY,
    TenVatTu NVARCHAR(100) NOT NULL,
    DonViTinh NVARCHAR(30) NOT NULL,
    SoLuongTon INT NOT NULL CONSTRAINT DF_VatTu_SoLuongTon DEFAULT 0,
    MucTonToiThieu INT NOT NULL CONSTRAINT DF_VatTu_MucTonToiThieu DEFAULT 0,
    TrangThai NVARCHAR(30) NOT NULL CONSTRAINT DF_VatTu_TrangThai DEFAULT N'Đang sử dụng',
    MoTa NVARCHAR(255) NULL,
    CONSTRAINT UQ_VatTu_Ten_DonVi UNIQUE (TenVatTu, DonViTinh),
    CONSTRAINT CK_VatTu_SoLuongTon CHECK (SoLuongTon >= 0),
    CONSTRAINT CK_VatTu_MucTonToiThieu CHECK (MucTonToiThieu >= 0),
    CONSTRAINT CK_VatTu_TrangThai CHECK (TrangThai IN (N'Đang sử dụng', N'Ngừng sử dụng'))
);

CREATE TABLE dbo.NhaCungCap (
    MaNhaCungCap VARCHAR(20) NOT NULL CONSTRAINT PK_NhaCungCap PRIMARY KEY,
    TenNhaCungCap NVARCHAR(150) NOT NULL,
    MaSoThue VARCHAR(20) NULL,
    SoDienThoai VARCHAR(20) NULL,
    Email VARCHAR(120) NULL,
    DiaChi NVARCHAR(255) NULL,
    TrangThai NVARCHAR(30) NOT NULL CONSTRAINT DF_NCC_TrangThai DEFAULT N'Đang hoạt động',
    GhiChu NVARCHAR(255) NULL,
    CONSTRAINT CK_NCC_TrangThai CHECK (TrangThai IN (N'Đang hoạt động', N'Ngừng hoạt động'))
);
GO
CREATE UNIQUE INDEX UX_NhaCungCap_MaSoThue ON dbo.NhaCungCap(MaSoThue) WHERE MaSoThue IS NOT NULL;
GO

CREATE TABLE dbo.PhieuNhap (
    MaPhieuNhap VARCHAR(20) NOT NULL CONSTRAINT PK_PhieuNhap PRIMARY KEY,
    MaNhaCungCap VARCHAR(20) NOT NULL,
    NgayNhap DATETIME2(0) NOT NULL,
    TongTien DECIMAL(18,2) NOT NULL CONSTRAINT DF_PhieuNhap_TongTien DEFAULT 0,
    TrangThai NVARCHAR(30) NOT NULL CONSTRAINT DF_PhieuNhap_TrangThai DEFAULT N'Nháp',
    NguoiLap NVARCHAR(100) NULL,
    GhiChu NVARCHAR(255) NULL,
    NgayTao DATETIME2(0) NOT NULL CONSTRAINT DF_PhieuNhap_NgayTao DEFAULT SYSDATETIME(),
    CONSTRAINT FK_PhieuNhap_NhaCungCap FOREIGN KEY (MaNhaCungCap) REFERENCES dbo.NhaCungCap(MaNhaCungCap),
    CONSTRAINT CK_PhieuNhap_TongTien CHECK (TongTien >= 0),
    CONSTRAINT CK_PhieuNhap_TrangThai CHECK (TrangThai IN (N'Nháp', N'Đã xác nhận', N'Đã hủy'))
);

CREATE TABLE dbo.ChiTietPhieuNhap (
    MaPhieuNhap VARCHAR(20) NOT NULL,
    MaVatTu VARCHAR(20) NOT NULL,
    SoLuong INT NOT NULL,
    DonGia DECIMAL(18,2) NOT NULL,
    ThanhTien AS (CONVERT(DECIMAL(18,2), SoLuong * DonGia)) PERSISTED,
    CONSTRAINT PK_ChiTietPhieuNhap PRIMARY KEY (MaPhieuNhap, MaVatTu),
    CONSTRAINT FK_CTPN_PhieuNhap FOREIGN KEY (MaPhieuNhap) REFERENCES dbo.PhieuNhap(MaPhieuNhap),
    CONSTRAINT FK_CTPN_VatTu FOREIGN KEY (MaVatTu) REFERENCES dbo.VatTu(MaVatTu),
    CONSTRAINT CK_CTPN_SoLuong CHECK (SoLuong > 0),
    CONSTRAINT CK_CTPN_DonGia CHECK (DonGia >= 0)
);
GO

/* ========================================================================
   NHOM 5: TAI CHINH
   ======================================================================== */
CREATE TABLE dbo.HoaDon (
    MaHoaDon VARCHAR(30) NOT NULL CONSTRAINT PK_HoaDon PRIMARY KEY,
    MaDatPhong VARCHAR(20) NOT NULL,
    NgayLap DATE NOT NULL CONSTRAINT DF_HoaDon_NgayLap DEFAULT CONVERT(DATE, GETDATE()),
    TienGiam DECIMAL(18,2) NOT NULL CONSTRAINT DF_HoaDon_TienGiam DEFAULT 0,
    TongTien DECIMAL(18,2) NOT NULL CONSTRAINT DF_HoaDon_TongTien DEFAULT 0,
    TrangThai NVARCHAR(50) NOT NULL CONSTRAINT DF_HoaDon_TrangThai DEFAULT N'Chưa thanh toán',
    CONSTRAINT FK_HoaDon_DatPhong FOREIGN KEY (MaDatPhong) REFERENCES dbo.DatPhong(MaDatPhong),
    CONSTRAINT CK_HoaDon_TienGiam CHECK (TienGiam >= 0),
    CONSTRAINT CK_HoaDon_TongTien CHECK (TongTien >= 0)
);

CREATE TABLE dbo.ChiTietHoaDon (
    MaChiTiet VARCHAR(30) NOT NULL CONSTRAINT PK_ChiTietHoaDon PRIMARY KEY,
    MaHoaDon VARCHAR(30) NOT NULL,
    MoTa NVARCHAR(500) NOT NULL,
    LoaiChiPhi NVARCHAR(50) NOT NULL,
    MaPhong VARCHAR(20) NULL,
    MaDichVu INT NULL,
    SoLuong INT NOT NULL,
    DonGia DECIMAL(18,2) NOT NULL,
    ThanhTien DECIMAL(18,2) NOT NULL,
    CONSTRAINT FK_ChiTietHoaDon_HoaDon FOREIGN KEY (MaHoaDon) REFERENCES dbo.HoaDon(MaHoaDon),
    CONSTRAINT FK_ChiTietHoaDon_Phong FOREIGN KEY (MaPhong) REFERENCES dbo.Phong(MaPhong),
    CONSTRAINT FK_ChiTietHoaDon_DichVu FOREIGN KEY (MaDichVu) REFERENCES dbo.DichVu(MaDV),
    CONSTRAINT CK_ChiTietHoaDon_SoLuong CHECK (SoLuong > 0),
    CONSTRAINT CK_ChiTietHoaDon_DonGia CHECK (DonGia >= 0),
    CONSTRAINT CK_ChiTietHoaDon_ThanhTien CHECK (ThanhTien >= 0)
);

CREATE TABLE dbo.ThanhToan (
    MaThanhToan VARCHAR(30) NOT NULL CONSTRAINT PK_ThanhToan PRIMARY KEY,
    MaHoaDon VARCHAR(30) NOT NULL,
    NgayThanhToan DATE NOT NULL CONSTRAINT DF_ThanhToan_Ngay DEFAULT CONVERT(DATE, GETDATE()),
    SoTien DECIMAL(18,2) NOT NULL,
    PhuongThuc NVARCHAR(50) NOT NULL,
    TrangThai NVARCHAR(50) NOT NULL,
    CONSTRAINT FK_ThanhToan_HoaDon FOREIGN KEY (MaHoaDon) REFERENCES dbo.HoaDon(MaHoaDon),
    CONSTRAINT CK_ThanhToan_SoTien CHECK (SoTien > 0)
);
GO

/* ========================================================================
   NHOM 5: PHAN HOI KHACH HANG
   ======================================================================== */
CREATE TABLE dbo.PhanHoi (
    MaPhanHoi BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_PhanHoi PRIMARY KEY,
    MaPhanHoiHienThi AS ('PH' + RIGHT('000000' + CONVERT(VARCHAR(6), MaPhanHoi), 6)) PERSISTED,
    MaKhachHang VARCHAR(20) NOT NULL,
    LoaiPhanHoi VARCHAR(20) NOT NULL,
    NoiDung NVARCHAR(2000) NOT NULL,
    ThoiGianTiepNhan DATETIME2(0) NOT NULL CONSTRAINT DF_PhanHoi_TiepNhan DEFAULT SYSDATETIME(),
    TrangThai VARCHAR(20) NOT NULL CONSTRAINT DF_PhanHoi_TrangThai DEFAULT 'MOI_TIEP_NHAN',
    ThoiGianCapNhat DATETIME2(0) NOT NULL CONSTRAINT DF_PhanHoi_CapNhat DEFAULT SYSDATETIME(),
    PhienBan ROWVERSION NOT NULL,
    CONSTRAINT UQ_PhanHoi_MaHienThi UNIQUE (MaPhanHoiHienThi),
    CONSTRAINT FK_PhanHoi_KhachHang FOREIGN KEY (MaKhachHang) REFERENCES dbo.KhachHang(MaKhachHang),
    CONSTRAINT CK_PhanHoi_Loai CHECK (LoaiPhanHoi IN ('DANH_GIA', 'KHIEU_NAI')),
    CONSTRAINT CK_PhanHoi_NoiDung CHECK (LEN(TRIM(NoiDung)) > 0),
    CONSTRAINT CK_PhanHoi_TrangThai CHECK (TrangThai IN ('MOI_TIEP_NHAN', 'DANG_XU_LY', 'DA_XU_LY'))
);

CREATE TABLE dbo.LichSuXuLyPhanHoi (
    MaLichSu BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_LichSuXuLyPhanHoi PRIMARY KEY,
    MaPhanHoi BIGINT NOT NULL,
    ThoiGianGhiNhan DATETIME2(0) NOT NULL CONSTRAINT DF_LichSuPhanHoi_ThoiGian DEFAULT SYSDATETIME(),
    TrangThaiTruoc VARCHAR(20) NULL,
    TrangThaiSau VARCHAR(20) NOT NULL,
    NoiDungXuLy NVARCHAR(2000) NOT NULL,
    NguoiThucHien NVARCHAR(100) NULL,
    CONSTRAINT FK_LichSuPhanHoi_PhanHoi FOREIGN KEY (MaPhanHoi) REFERENCES dbo.PhanHoi(MaPhanHoi),
    CONSTRAINT CK_LichSuPhanHoi_NoiDung CHECK (LEN(TRIM(NoiDungXuLy)) > 0)
);
GO

/* ========================================================================
   NGHIEP VU DAT / NHAN / TRA / DOI PHONG
   ======================================================================== */
CREATE PROCEDURE dbo.sp_DatPhong_TaoMoi
    @MaDatPhong VARCHAR(MAX) = NULL,
    @MaKhachHang VARCHAR(MAX),
    @MaPhong VARCHAR(MAX),
    @GiaPhongChot DECIMAL(18,4),
    @NgayNhanDuKien DATETIME2(0),
    @NgayTraDuKien DATETIME2(0),
    @MaLuotLuuTru VARCHAR(MAX) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

    DECLARE @MaDP VARCHAR(20) = NULLIF(TRIM(@MaDatPhong), '');
    DECLARE @MaKH VARCHAR(20) = TRIM(@MaKhachHang);
    DECLARE @MaP VARCHAR(20) = TRIM(@MaPhong);
    DECLARE @MaLT VARCHAR(20) = NULLIF(TRIM(@MaLuotLuuTru), '');

    IF @MaKH IS NULL OR @MaP IS NULL
        THROW 52001, N'Khach hang va phong khong duoc de trong.', 1;
    IF @NgayNhanDuKien IS NULL OR @NgayTraDuKien <= @NgayNhanDuKien
        THROW 52002, N'Thoi gian nhan/tra du kien khong hop le.', 1;
    IF @GiaPhongChot IS NULL OR @GiaPhongChot < 0
        THROW 52003, N'Gia phong chot khong hop le.', 1;
    IF @MaDP IS NOT NULL AND LEN(@MaDP) > 20
        THROW 52004, N'Ma dat phong qua dai.', 1;

    BEGIN TRY
        BEGIN TRANSACTION;

        IF NOT EXISTS (SELECT 1 FROM dbo.KhachHang WITH (UPDLOCK, HOLDLOCK)
                       WHERE MaKhachHang = @MaKH AND TrangThai = 'HOAT_DONG')
            THROW 52005, N'Khach hang khong ton tai hoac da ngung hoat dong.', 1;

        DECLARE @TrangThaiPhong VARCHAR(20), @NgungSuDung BIT;
        SELECT @TrangThaiPhong = TrangThai, @NgungSuDung = NgungSuDung
        FROM dbo.Phong WITH (UPDLOCK, HOLDLOCK)
        WHERE MaPhong = @MaP;

        IF @TrangThaiPhong IS NULL
            THROW 52006, N'Phong khong ton tai.', 1;
        IF @NgungSuDung = 1 OR @TrangThaiPhong IN ('DANG_SU_DUNG', 'DANG_DON', 'BAO_TRI')
            THROW 52007, N'Phong khong san sang de dat.', 1;

        IF EXISTS (
            SELECT 1 FROM dbo.DatPhong WITH (UPDLOCK, HOLDLOCK)
            WHERE MaPhong = @MaP
              AND TrangThai IN ('GIU_PHONG', 'DANG_O')
              AND @NgayNhanDuKien < NgayTraDuKien
              AND @NgayTraDuKien > NgayNhanDuKien
        )
            THROW 52008, N'Phong da co lich trung trong khoang da chon.', 1;

        -- Chi cap ma sau khi tat ca kiem tra nghiep vu da thanh cong.
        IF @MaDP IS NULL
        BEGIN
            DECLARE @SoDatPhong BIGINT = NEXT VALUE FOR dbo.SeqDatPhong;
            SET @MaDP = CONCAT('DP', RIGHT('000000' + CONVERT(VARCHAR(30), @SoDatPhong), 6));
        END;

        IF EXISTS (SELECT 1 FROM dbo.MaDatPhongDaCap WITH (UPDLOCK, HOLDLOCK)
                   WHERE MaDatPhong = @MaDP)
            THROW 52009, N'Ma dat phong da duoc su dung.', 1;

        INSERT dbo.MaDatPhongDaCap(MaDatPhong) VALUES (@MaDP);
        INSERT dbo.DatPhong
            (MaDatPhong, MaKhachHang, MaPhong, GiaPhongChot,
             NgayNhanDuKien, NgayTraDuKien, TrangThai, MaLuotLuuTru)
        VALUES
            (@MaDP, @MaKH, @MaP, CONVERT(DECIMAL(18,2), @GiaPhongChot),
             @NgayNhanDuKien, @NgayTraDuKien, 'GIU_PHONG', @MaLT);

        COMMIT TRANSACTION;
        SELECT @MaDP AS MaDatPhong, 'GIU_PHONG' AS TrangThai;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH;
END;
GO

CREATE PROCEDURE dbo.sp_DatPhong_CapNhatLich
    @MaDatPhong VARCHAR(MAX),
    @NgayNhanDuKien DATETIME2(0),
    @NgayTraDuKien DATETIME2(0),
    @NgayNhanGoc DATETIME2(0),
    @NgayTraGoc DATETIME2(0)
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    DECLARE @MaDP VARCHAR(20) = TRIM(@MaDatPhong);
    IF @NgayNhanDuKien IS NULL OR @NgayTraDuKien <= @NgayNhanDuKien
        THROW 52101, N'Thoi gian nhan/tra du kien khong hop le.', 1;

    BEGIN TRY
        BEGIN TRANSACTION;
        DECLARE @MaPhong VARCHAR(20), @TrangThai VARCHAR(20);
        SELECT @MaPhong = MaPhong, @TrangThai = TrangThai
        FROM dbo.DatPhong WITH (UPDLOCK, HOLDLOCK)
        WHERE MaDatPhong = @MaDP;
        IF @TrangThai IS NULL THROW 52102, N'Don dat phong khong ton tai.', 1;
        IF @TrangThai <> 'GIU_PHONG' THROW 52103, N'Chi don dang giu phong moi duoc sua lich.', 1;
        IF EXISTS (
            SELECT 1 FROM dbo.DatPhong
            WHERE MaPhong = @MaPhong AND MaDatPhong <> @MaDP
              AND TrangThai IN ('GIU_PHONG', 'DANG_O')
              AND @NgayNhanDuKien < NgayTraDuKien
              AND @NgayTraDuKien > NgayNhanDuKien
        ) THROW 52104, N'Lich moi bi trung voi mot don khac.', 1;

        UPDATE dbo.DatPhong
        SET NgayNhanDuKien = @NgayNhanDuKien, NgayTraDuKien = @NgayTraDuKien
        WHERE MaDatPhong = @MaDP AND TrangThai = 'GIU_PHONG'
          AND NgayNhanDuKien = @NgayNhanGoc AND NgayTraDuKien = @NgayTraGoc;
        IF @@ROWCOUNT <> 1 THROW 52105, N'Du lieu da thay doi; hay tai lai truoc khi sua.', 1;
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH;
END;
GO

CREATE PROCEDURE dbo.sp_DatPhong_Huy @MaDatPhong VARCHAR(MAX)
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE dbo.DatPhong SET TrangThai = 'DA_HUY'
    WHERE MaDatPhong = TRIM(@MaDatPhong) AND TrangThai = 'GIU_PHONG';
    IF @@ROWCOUNT <> 1
        THROW 52201, N'Chi don dang giu phong moi duoc huy.', 1;
END;
GO

CREATE PROCEDURE dbo.sp_DatPhong_NhanPhong
    @MaDatPhong VARCHAR(MAX),
    @ThoiDiemNhan DATETIME2(0) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    DECLARE @MaDP VARCHAR(20) = TRIM(@MaDatPhong);
    DECLARE @LucNhan DATETIME2(0) = COALESCE(@ThoiDiemNhan, SYSDATETIME());
    BEGIN TRY
        BEGIN TRANSACTION;
        DECLARE @MaPhong VARCHAR(20), @TrangThai VARCHAR(20), @NgayNhanDuKien DATETIME2(0);
        SELECT @MaPhong = MaPhong, @TrangThai = TrangThai, @NgayNhanDuKien = NgayNhanDuKien
        FROM dbo.DatPhong WITH (UPDLOCK, HOLDLOCK) WHERE MaDatPhong = @MaDP;
        IF @TrangThai IS NULL OR @TrangThai <> 'GIU_PHONG'
            THROW 52301, N'Don khong o trang thai cho nhan phong.', 1;
        IF @LucNhan < @NgayNhanDuKien THROW 52302, N'Chua den thoi diem nhan phong du kien.', 1;
        IF NOT EXISTS (SELECT 1 FROM dbo.Phong WITH (UPDLOCK, HOLDLOCK)
                       WHERE MaPhong = @MaPhong AND NgungSuDung = 0
                         AND TrangThai IN ('TRONG', 'DA_DAT'))
            THROW 52303, N'Phong khong san sang de nhan.', 1;

        UPDATE dbo.DatPhong SET TrangThai = 'DANG_O', NgayNhan = @LucNhan
        WHERE MaDatPhong = @MaDP AND TrangThai = 'GIU_PHONG';
        UPDATE dbo.Phong SET TrangThai = 'DANG_SU_DUNG' WHERE MaPhong = @MaPhong;
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH;
END;
GO

CREATE PROCEDURE dbo.sp_DatPhong_TraPhong
    @MaDatPhong VARCHAR(MAX),
    @ThoiDiemTra DATETIME2(0) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    DECLARE @MaDP VARCHAR(20) = TRIM(@MaDatPhong);
    DECLARE @LucTra DATETIME2(0) = COALESCE(@ThoiDiemTra, SYSDATETIME());
    BEGIN TRY
        BEGIN TRANSACTION;
        DECLARE @MaPhong VARCHAR(20), @TrangThai VARCHAR(20), @NgayNhan DATETIME2(0);
        SELECT @MaPhong = MaPhong, @TrangThai = TrangThai, @NgayNhan = NgayNhan
        FROM dbo.DatPhong WITH (UPDLOCK, HOLDLOCK) WHERE MaDatPhong = @MaDP;
        IF @TrangThai IS NULL OR @TrangThai <> 'DANG_O'
            THROW 52401, N'Chi don dang o moi duoc tra phong.', 1;
        IF @NgayNhan IS NULL OR @LucTra <= @NgayNhan THROW 52402, N'Thoi diem tra khong hop le.', 1;
        UPDATE dbo.DatPhong SET TrangThai = 'DA_TRA', NgayTra = @LucTra
        WHERE MaDatPhong = @MaDP AND TrangThai = 'DANG_O';
        -- Tra phong xong phai don phong truoc khi mo lai trang thai TRONG.
        UPDATE dbo.Phong SET TrangThai = 'DANG_DON' WHERE MaPhong = @MaPhong;
        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH;
END;
GO

CREATE PROCEDURE dbo.sp_DatPhong_DoiPhong
    @MaDatPhongCu VARCHAR(MAX),
    @MaPhongMoi VARCHAR(MAX),
    @LyDo NVARCHAR(1000),
    @NguoiThucHien NVARCHAR(100) = NULL,
    @MaDatPhongMoi VARCHAR(MAX),
    @MaChuyenPhong VARCHAR(MAX)
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    DECLARE @MaCu VARCHAR(20) = TRIM(@MaDatPhongCu);
    DECLARE @PhongMoi VARCHAR(20) = TRIM(@MaPhongMoi);
    DECLARE @MaMoi VARCHAR(20) = TRIM(@MaDatPhongMoi);
    DECLARE @MaChuyen VARCHAR(20) = TRIM(@MaChuyenPhong);
    DECLARE @LucChuyen DATETIME2(0) = SYSDATETIME();
    IF @MaCu IS NULL OR @PhongMoi IS NULL OR @MaMoi IS NULL OR @MaChuyen IS NULL
        THROW 52501, N'Thieu ma bat buoc khi doi phong.', 1;
    IF NULLIF(TRIM(@LyDo), N'') IS NULL THROW 52502, N'Ly do doi phong khong duoc de trong.', 1;

    BEGIN TRY
        BEGIN TRANSACTION;
        DECLARE @MaKH VARCHAR(20), @PhongCu VARCHAR(20), @Nhan DATETIME2(0),
                @Tra DATETIME2(0), @TrangThaiCu VARCHAR(20), @MaLT VARCHAR(20);
        SELECT @MaKH = MaKhachHang, @PhongCu = MaPhong, @Nhan = NgayNhanDuKien,
               @Tra = NgayTraDuKien, @TrangThaiCu = TrangThai, @MaLT = MaLuotLuuTru
        FROM dbo.DatPhong WITH (UPDLOCK, HOLDLOCK) WHERE MaDatPhong = @MaCu;
        IF @TrangThaiCu IS NULL OR @TrangThaiCu NOT IN ('GIU_PHONG', 'DANG_O')
            THROW 52503, N'Chi don giu phong hoac dang o moi duoc doi phong.', 1;
        IF @PhongCu = @PhongMoi THROW 52504, N'Phong moi trung phong cu.', 1;
        IF EXISTS (SELECT 1 FROM dbo.MaDatPhongDaCap WITH (UPDLOCK, HOLDLOCK)
                   WHERE MaDatPhong = @MaMoi)
            THROW 52505, N'Ma dat phong moi da duoc su dung.', 1;
        IF EXISTS (SELECT 1 FROM dbo.ChuyenPhong WITH (UPDLOCK, HOLDLOCK)
                   WHERE MaChuyenPhong = @MaChuyen)
            THROW 52506, N'Ma chuyen phong da ton tai.', 1;
        IF NOT EXISTS (SELECT 1 FROM dbo.Phong WITH (UPDLOCK, HOLDLOCK)
                       WHERE MaPhong = @PhongMoi AND NgungSuDung = 0
                         AND TrangThai IN ('TRONG', 'DA_DAT'))
            THROW 52507, N'Phong moi khong san sang.', 1;

        DECLARE @BatDauMoi DATETIME2(0) = CASE WHEN @TrangThaiCu = 'DANG_O' THEN @LucChuyen ELSE @Nhan END;
        IF EXISTS (
            SELECT 1 FROM dbo.DatPhong
            WHERE MaPhong = @PhongMoi AND TrangThai IN ('GIU_PHONG', 'DANG_O')
              AND @BatDauMoi < NgayTraDuKien AND @Tra > NgayNhanDuKien
        ) THROW 52508, N'Phong moi bi trung lich.', 1;

        DECLARE @GiaMoi DECIMAL(18,2);
        SELECT @GiaMoi = COALESCE(p.GiaPhong, lp.GiaTheoNgay)
        FROM dbo.Phong p JOIN dbo.LoaiPhong lp ON lp.MaLoaiPhong = p.MaLoaiPhong
        WHERE p.MaPhong = @PhongMoi;

        INSERT dbo.MaDatPhongDaCap(MaDatPhong) VALUES (@MaMoi);
        INSERT dbo.DatPhong
            (MaDatPhong, MaKhachHang, MaPhong, GiaPhongChot, NgayNhan,
             NgayNhanDuKien, NgayTraDuKien, TrangThai, MaLuotLuuTru)
        VALUES
            (@MaMoi, @MaKH, @PhongMoi, @GiaMoi,
             CASE WHEN @TrangThaiCu = 'DANG_O' THEN @LucChuyen END,
             @BatDauMoi, @Tra, @TrangThaiCu, @MaLT);

        INSERT dbo.ChuyenPhong
            (MaChuyenPhong, MaDatPhongCu, MaDatPhongMoi, LyDo, NguoiThucHien, ThoiGian)
        VALUES (@MaChuyen, @MaCu, @MaMoi, TRIM(@LyDo), NULLIF(TRIM(@NguoiThucHien), N''), @LucChuyen);

        UPDATE dbo.DatPhong
        SET TrangThai = CASE WHEN @TrangThaiCu = 'DANG_O' THEN 'DA_TRA' ELSE 'DA_HUY' END,
            NgayTra = CASE WHEN @TrangThaiCu = 'DANG_O' THEN @LucChuyen ELSE NgayTra END
        WHERE MaDatPhong = @MaCu;

        IF @TrangThaiCu = 'DANG_O'
        BEGIN
            UPDATE dbo.Phong SET TrangThai = 'DANG_DON' WHERE MaPhong = @PhongCu;
            UPDATE dbo.Phong SET TrangThai = 'DANG_SU_DUNG' WHERE MaPhong = @PhongMoi;
        END;

        COMMIT TRANSACTION;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
        THROW;
    END CATCH;
END;
GO

CREATE PROCEDURE dbo.sp_DatPhong_Xoa @MaDatPhong VARCHAR(MAX)
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    DECLARE @MaDP VARCHAR(20) = TRIM(@MaDatPhong);
    IF NOT EXISTS (SELECT 1 FROM dbo.DatPhong WHERE MaDatPhong = @MaDP AND TrangThai = 'DA_HUY')
        THROW 52601, N'Chi don da huy moi duoc xoa.', 1;
    IF EXISTS (SELECT 1 FROM dbo.ChuyenPhong WHERE MaDatPhongCu = @MaDP OR MaDatPhongMoi = @MaDP)
       OR EXISTS (SELECT 1 FROM dbo.PhongLuuTruNhan WHERE MaDatPhong = @MaDP)
       OR EXISTS (SELECT 1 FROM dbo.SuDungDichVu WHERE MaDatPhong = @MaDP)
       OR EXISTS (SELECT 1 FROM dbo.YeuCauDatPhong WHERE MaDatPhong = @MaDP)
       OR EXISTS (SELECT 1 FROM dbo.HoaDon WHERE MaDatPhong = @MaDP)
        THROW 52602, N'Don co du lieu lien quan nen khong the xoa.', 1;
    DELETE dbo.DatPhong WHERE MaDatPhong = @MaDP AND TrangThai = 'DA_HUY';
END;
GO

/* ========================================================================
   INDEX TRA CUU
   ======================================================================== */
CREATE INDEX IX_DatPhong_KhachHang_Ngay ON dbo.DatPhong(MaKhachHang, NgayDat DESC);
CREATE INDEX IX_DatPhong_Phong_Lich ON dbo.DatPhong(MaPhong, TrangThai, NgayNhanDuKien, NgayTraDuKien);
CREATE INDEX IX_VeSinh_Phong_TrangThai ON dbo.VeSinh(MaPhong, TrangThai);
CREATE INDEX IX_BaoTri_Phong_TrangThai ON dbo.BaoTri(MaPhong, TrangThai);
CREATE INDEX IX_SuDungDichVu_DatPhong ON dbo.SuDungDichVu(MaDatPhong, ThoiGianSuDung);
CREATE INDEX IX_VatTu_TrangThai_TonKho ON dbo.VatTu(TrangThai, SoLuongTon, MucTonToiThieu);
CREATE INDEX IX_PhieuNhap_NCC_NgayNhap ON dbo.PhieuNhap(MaNhaCungCap, NgayNhap DESC);
CREATE INDEX IX_HoaDon_NgayLap ON dbo.HoaDon(NgayLap);
CREATE INDEX IX_HoaDon_MaDatPhong ON dbo.HoaDon(MaDatPhong);
CREATE INDEX IX_ChiTietHoaDon_MaHoaDon ON dbo.ChiTietHoaDon(MaHoaDon);
CREATE INDEX IX_ThanhToan_MaHoaDon ON dbo.ThanhToan(MaHoaDon);
CREATE INDEX IX_ThanhToan_Ngay ON dbo.ThanhToan(NgayThanhToan);
CREATE INDEX IX_PhanHoi_KhachHang ON dbo.PhanHoi(MaKhachHang);
CREATE INDEX IX_PhanHoi_Loc ON dbo.PhanHoi(TrangThai, LoaiPhanHoi, ThoiGianTiepNhan DESC);
CREATE INDEX IX_LichSuPhanHoi_ThoiGian
    ON dbo.LichSuXuLyPhanHoi(MaPhanHoi, ThoiGianGhiNhan, MaLichSu);
GO

PRINT N'Da tao xong database QuanLyKhachSan: 31 bang, 20 sequence va 8 thu tuc.';
GO
