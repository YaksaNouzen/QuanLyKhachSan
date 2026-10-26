/*
    MODULE KHO & CUNG ỨNG - SQL SERVER
    Database dùng chung: QLHDKhachSan

    Chạy file này một lần trong SSMS sau khi đã tạo database QLHDKhachSan.
    Script chỉ tạo các bảng chưa tồn tại, không xóa hay sửa dữ liệu hiện có.
*/
USE QLHDKhachSan;
GO

SET XACT_ABORT ON;
BEGIN TRANSACTION;

IF OBJECT_ID(N'dbo.VatTu', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.VatTu (
        MaVatTu        VARCHAR(20)    NOT NULL,
        TenVatTu       NVARCHAR(100)  NOT NULL,
        DonViTinh      NVARCHAR(30)   NOT NULL,
        SoLuongTon     INT            NOT NULL CONSTRAINT DF_VatTu_SoLuongTon DEFAULT 0,
        MucTonToiThieu INT            NOT NULL CONSTRAINT DF_VatTu_MucTonToiThieu DEFAULT 0,
        TrangThai      NVARCHAR(30)   NOT NULL CONSTRAINT DF_VatTu_TrangThai DEFAULT N'Đang sử dụng',
        MoTa           NVARCHAR(255)  NULL,
        CONSTRAINT PK_VatTu PRIMARY KEY (MaVatTu),
        CONSTRAINT CK_VatTu_SoLuongTon CHECK (SoLuongTon >= 0),
        CONSTRAINT CK_VatTu_MucTonToiThieu CHECK (MucTonToiThieu >= 0),
        CONSTRAINT CK_VatTu_TrangThai CHECK (
            TrangThai IN (N'Đang sử dụng', N'Ngừng sử dụng'))
    );
END;

/*
   Tương thích với lần chạy cũ: đổi "Đang hoạt động" thành "Đang sử dụng".
   Khối này an toàn khi chạy lại nhiều lần.
*/
IF OBJECT_ID(N'dbo.VatTu', N'U') IS NOT NULL
BEGIN
    IF EXISTS (SELECT 1 FROM sys.check_constraints
               WHERE name = N'CK_VatTu_TrangThai'
                 AND parent_object_id = OBJECT_ID(N'dbo.VatTu'))
        ALTER TABLE dbo.VatTu DROP CONSTRAINT CK_VatTu_TrangThai;

    UPDATE dbo.VatTu
    SET TrangThai = N'Đang sử dụng'
    WHERE TrangThai = N'Đang hoạt động';

    IF EXISTS (SELECT 1 FROM sys.default_constraints
               WHERE name = N'DF_VatTu_TrangThai'
                 AND parent_object_id = OBJECT_ID(N'dbo.VatTu'))
        ALTER TABLE dbo.VatTu DROP CONSTRAINT DF_VatTu_TrangThai;

    ALTER TABLE dbo.VatTu ADD CONSTRAINT DF_VatTu_TrangThai
        DEFAULT N'Đang sử dụng' FOR TrangThai;
    ALTER TABLE dbo.VatTu ADD CONSTRAINT CK_VatTu_TrangThai
        CHECK (TrangThai IN (N'Đang sử dụng', N'Ngừng sử dụng'));
END;

IF OBJECT_ID(N'dbo.NhaCungCap', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.NhaCungCap (
        MaNhaCungCap  VARCHAR(20)    NOT NULL,
        TenNhaCungCap NVARCHAR(150)  NOT NULL,
        SoDienThoai   VARCHAR(20)    NULL,
        Email         VARCHAR(120)   NULL,
        DiaChi        NVARCHAR(255)  NULL,
        TrangThai     NVARCHAR(30)   NOT NULL CONSTRAINT DF_NCC_TrangThai DEFAULT N'Đang hoạt động',
        GhiChu        NVARCHAR(255)  NULL,
        CONSTRAINT PK_NhaCungCap PRIMARY KEY (MaNhaCungCap),
        CONSTRAINT CK_NCC_TrangThai CHECK (
            TrangThai IN (N'Đang hoạt động', N'Ngừng hoạt động'))
    );
END;

IF OBJECT_ID(N'dbo.PhieuNhap', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.PhieuNhap (
        MaPhieuNhap   VARCHAR(20)    NOT NULL,
        MaNhaCungCap  VARCHAR(20)    NOT NULL,
        NgayNhap      DATETIME2(0)   NOT NULL,
        TongTien      DECIMAL(18,2)  NOT NULL CONSTRAINT DF_PhieuNhap_TongTien DEFAULT 0,
        TrangThai     NVARCHAR(30)   NOT NULL CONSTRAINT DF_PhieuNhap_TrangThai DEFAULT N'Nháp',
        NguoiLap      NVARCHAR(100)  NULL,
        GhiChu        NVARCHAR(255)  NULL,
        NgayTao       DATETIME2(0)   NOT NULL CONSTRAINT DF_PhieuNhap_NgayTao DEFAULT SYSDATETIME(),
        CONSTRAINT PK_PhieuNhap PRIMARY KEY (MaPhieuNhap),
        CONSTRAINT FK_PhieuNhap_NhaCungCap FOREIGN KEY (MaNhaCungCap)
            REFERENCES dbo.NhaCungCap(MaNhaCungCap),
        CONSTRAINT CK_PhieuNhap_TongTien CHECK (TongTien >= 0),
        CONSTRAINT CK_PhieuNhap_TrangThai CHECK (
            TrangThai IN (N'Nháp', N'Đã xác nhận', N'Đã hủy'))
    );
END;

IF OBJECT_ID(N'dbo.ChiTietPhieuNhap', N'U') IS NULL
BEGIN
    CREATE TABLE dbo.ChiTietPhieuNhap (
        MaPhieuNhap VARCHAR(20)    NOT NULL,
        MaVatTu     VARCHAR(20)    NOT NULL,
        SoLuong     INT            NOT NULL,
        DonGia      DECIMAL(18,2)  NOT NULL,
        ThanhTien   AS (CONVERT(DECIMAL(18,2), SoLuong * DonGia)) PERSISTED,
        CONSTRAINT PK_ChiTietPhieuNhap PRIMARY KEY (MaPhieuNhap, MaVatTu),
        CONSTRAINT FK_CTPN_PhieuNhap FOREIGN KEY (MaPhieuNhap)
            REFERENCES dbo.PhieuNhap(MaPhieuNhap),
        CONSTRAINT FK_CTPN_VatTu FOREIGN KEY (MaVatTu)
            REFERENCES dbo.VatTu(MaVatTu),
        CONSTRAINT CK_CTPN_SoLuong CHECK (SoLuong > 0),
        CONSTRAINT CK_CTPN_DonGia CHECK (DonGia >= 0)
    );
END;

IF NOT EXISTS (SELECT 1 FROM sys.indexes
               WHERE name = N'IX_VatTu_TrangThai_TonKho'
                 AND object_id = OBJECT_ID(N'dbo.VatTu'))
    CREATE INDEX IX_VatTu_TrangThai_TonKho
        ON dbo.VatTu(TrangThai, SoLuongTon, MucTonToiThieu);

IF NOT EXISTS (SELECT 1 FROM sys.indexes
               WHERE name = N'IX_PhieuNhap_NCC_NgayNhap'
                 AND object_id = OBJECT_ID(N'dbo.PhieuNhap'))
    CREATE INDEX IX_PhieuNhap_NCC_NgayNhap
        ON dbo.PhieuNhap(MaNhaCungCap, NgayNhap DESC);

COMMIT TRANSACTION;
GO

/* Dữ liệu thử nghiệm: bỏ chú thích nếu cần kiểm tra giao diện.
INSERT INTO dbo.VatTu
    (MaVatTu, TenVatTu, DonViTinh, MucTonToiThieu, TrangThai, MoTa)
VALUES
    ('VT001', N'Khăn tắm', N'Cái', 20, N'Đang sử dụng', N'Khăn tắm khách sạn'),
    ('VT002', N'Nước suối', N'Chai', 50, N'Đang sử dụng', N'Nước đặt trong phòng');

INSERT INTO dbo.NhaCungCap
    (MaNhaCungCap, TenNhaCungCap, SoDienThoai, Email, DiaChi, TrangThai)
VALUES
    ('NCC001', N'Công ty cung ứng Minh Anh', '0900000001',
     'minhanh@example.com', N'TP. Hồ Chí Minh', N'Đang hoạt động');
*/
