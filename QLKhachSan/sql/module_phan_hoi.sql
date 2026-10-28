-- Execute manually in SQL Server Management Studio. No customer table is created.
-- No sample data, DROP, DELETE, or changes to finance/service databases.
USE master;
GO
IF DB_ID(N'QLPhanHoi') IS NULL
    EXEC(N'CREATE DATABASE QLPhanHoi');
GO
USE QLPhanHoi;
GO
-- Guard against a failed USE batch leaving the session in another database.
IF DB_NAME() <> N'QLPhanHoi'
    THROW 51001, N'Sai database đích. Không thực hiện tạo bảng.', 1;
SET XACT_ABORT ON;
BEGIN TRY
    BEGIN TRANSACTION;
    IF OBJECT_ID(N'dbo.PhanHoi', N'U') IS NOT NULL
       OR OBJECT_ID(N'dbo.LichSuXuLyPhanHoi', N'U') IS NOT NULL
        THROW 51000, N'Bảng Phản hồi đã tồn tại. Dừng để bảo vệ dữ liệu; không tự thay thế schema.', 1;

    CREATE TABLE dbo.PhanHoi (
        MaPhanHoi BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_PhanHoi PRIMARY KEY,
        -- Reference only: add FK after confirming the shared customer database/schema.
        MaKhachHang VARCHAR(20) NOT NULL,
        LoaiPhanHoi VARCHAR(20) NOT NULL,
        NoiDung NVARCHAR(2000) NOT NULL,
        ThoiGianTiepNhan DATETIME2(0) NOT NULL CONSTRAINT DF_PhanHoi_TiepNhan DEFAULT SYSDATETIME(),
        TrangThai VARCHAR(20) NOT NULL CONSTRAINT DF_PhanHoi_TrangThai DEFAULT 'MOI_TIEP_NHAN',
        ThoiGianCapNhat DATETIME2(0) NOT NULL CONSTRAINT DF_PhanHoi_CapNhat DEFAULT SYSDATETIME(),
        PhienBan ROWVERSION NOT NULL,
        CONSTRAINT CK_PhanHoi_KhachHang CHECK (LEN(LTRIM(RTRIM(MaKhachHang))) > 0),
        CONSTRAINT CK_PhanHoi_Loai CHECK (LoaiPhanHoi IN ('DANH_GIA','KHIEU_NAI')),
        CONSTRAINT CK_PhanHoi_NoiDung CHECK (LEN(LTRIM(RTRIM(NoiDung))) > 0),
        CONSTRAINT CK_PhanHoi_TrangThai CHECK (TrangThai IN ('MOI_TIEP_NHAN','DANG_XU_LY','DA_XU_LY'))
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
        CONSTRAINT CK_LichSuPhanHoi_NoiDung CHECK (LEN(LTRIM(RTRIM(NoiDungXuLy))) > 0),
        CONSTRAINT CK_LichSuPhanHoi_Nguoi CHECK (NguoiThucHien IS NULL OR LEN(LTRIM(RTRIM(NguoiThucHien))) > 0),
        CONSTRAINT CK_LichSuPhanHoi_ChuyenTrangThai CHECK (
            (TrangThaiTruoc IS NULL AND TrangThaiSau = 'MOI_TIEP_NHAN') OR
            (TrangThaiTruoc IS NOT NULL AND TrangThaiTruoc IN ('MOI_TIEP_NHAN','DANG_XU_LY')
                AND TrangThaiSau IN ('DANG_XU_LY','DA_XU_LY')))
    );
    CREATE INDEX IX_PhanHoi_KhachHang ON dbo.PhanHoi(MaKhachHang);
    CREATE INDEX IX_PhanHoi_Loc ON dbo.PhanHoi(TrangThai,LoaiPhanHoi,ThoiGianTiepNhan DESC);
    CREATE INDEX IX_LichSuPhanHoi_ThoiGian ON dbo.LichSuXuLyPhanHoi(MaPhanHoi,ThoiGianGhiNhan,MaLichSu);
    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO
