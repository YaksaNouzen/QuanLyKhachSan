-- Patch N2_001: bo sung vong doi buong phong va bao tri.
-- Database nen: schema.sql tren nhanh QuanLyDatPhong.
-- Chon database thu nghiem truoc khi chay; script co the chay lai.

SET NOCOUNT ON;
SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    IF COL_LENGTH(N'dbo.VeSinh', N'TrangThai') IS NULL
    BEGIN
        ALTER TABLE dbo.VeSinh
            ADD TrangThai varchar(20) NOT NULL
                CONSTRAINT DF_VeSinh_TrangThai_N2 DEFAULT 'CHO_DON';
        UPDATE dbo.VeSinh
        SET TrangThai = CASE WHEN KetThuc IS NULL THEN 'DANG_DON' ELSE 'HOAN_THANH' END;
    END;

    IF COL_LENGTH(N'dbo.VeSinh', N'NgayPhanCong') IS NULL
        ALTER TABLE dbo.VeSinh
            ADD NgayPhanCong datetime2(0) NOT NULL
                CONSTRAINT DF_VeSinh_NgayPhanCong_N2 DEFAULT SYSDATETIME();

    IF COL_LENGTH(N'dbo.VeSinh', N'NguoiXacNhan') IS NULL
        ALTER TABLE dbo.VeSinh ADD NguoiXacNhan nvarchar(100) NULL;

    IF COL_LENGTH(N'dbo.VeSinh', N'XacNhanLuc') IS NULL
        ALTER TABLE dbo.VeSinh ADD XacNhanLuc datetime2(0) NULL;

    IF EXISTS (
        SELECT 1 FROM sys.columns
        WHERE object_id = OBJECT_ID(N'dbo.VeSinh')
          AND name = N'BatDau' AND is_nullable = 0
    )
        ALTER TABLE dbo.VeSinh ALTER COLUMN BatDau datetime2(0) NULL;

    IF COL_LENGTH(N'dbo.BaoTri', N'TrangThai') IS NULL
    BEGIN
        ALTER TABLE dbo.BaoTri
            ADD TrangThai varchar(20) NOT NULL
                CONSTRAINT DF_BaoTri_TrangThai_N2 DEFAULT 'BAO_HONG';
        UPDATE dbo.BaoTri
        SET TrangThai = CASE WHEN KetThuc IS NULL THEN 'DANG_SUA' ELSE 'HOAN_TAT' END;
    END;

    IF COL_LENGTH(N'dbo.BaoTri', N'MucDo') IS NULL
        ALTER TABLE dbo.BaoTri
            ADD MucDo varchar(20) NOT NULL
                CONSTRAINT DF_BaoTri_MucDo_N2 DEFAULT 'TRUNG_BINH';

    IF COL_LENGTH(N'dbo.BaoTri', N'NguoiBao') IS NULL
    BEGIN
        ALTER TABLE dbo.BaoTri
            ADD NguoiBao nvarchar(100) NOT NULL
                CONSTRAINT DF_BaoTri_NguoiBao_N2 DEFAULT N'Chua ro';
        UPDATE dbo.BaoTri
        SET NguoiBao = COALESCE(NguoiPhuTrach, N'Chua ro');
    END;

    IF COL_LENGTH(N'dbo.BaoTri', N'NgayBao') IS NULL
    BEGIN
        ALTER TABLE dbo.BaoTri
            ADD NgayBao datetime2(0) NOT NULL
                CONSTRAINT DF_BaoTri_NgayBao_N2 DEFAULT SYSDATETIME();
        UPDATE dbo.BaoTri
        SET NgayBao = COALESCE(BatDau, SYSDATETIME());
    END;

    IF COL_LENGTH(N'dbo.BaoTri', N'ChiPhi') IS NULL
        ALTER TABLE dbo.BaoTri
            ADD ChiPhi decimal(18,2) NOT NULL
                CONSTRAINT DF_BaoTri_ChiPhi_N2 DEFAULT 0;

    IF COL_LENGTH(N'dbo.BaoTri', N'KetQua') IS NULL
        ALTER TABLE dbo.BaoTri
            ADD KetQua nvarchar(500) NOT NULL
                CONSTRAINT DF_BaoTri_KetQua_N2 DEFAULT N'';

    IF EXISTS (
        SELECT 1 FROM sys.columns
        WHERE object_id = OBJECT_ID(N'dbo.BaoTri')
          AND name = N'BatDau' AND is_nullable = 0
    )
        ALTER TABLE dbo.BaoTri ALTER COLUMN BatDau datetime2(0) NULL;

    IF EXISTS (
        SELECT 1 FROM sys.columns
        WHERE object_id = OBJECT_ID(N'dbo.BaoTri')
          AND name = N'NguoiPhuTrach' AND is_nullable = 0
    )
        ALTER TABLE dbo.BaoTri ALTER COLUMN NguoiPhuTrach nvarchar(100) NULL;

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO

-- Kiem tra nhanh cac cot sau khi chay.
SELECT TOP 0 TrangThai, NgayPhanCong, NguoiXacNhan, XacNhanLuc, BatDau
FROM dbo.VeSinh;
SELECT TOP 0 TrangThai, MucDo, NguoiBao, NgayBao, NguoiPhuTrach,
              BatDau, KetThuc, ChiPhi, KetQua
FROM dbo.BaoTri;
