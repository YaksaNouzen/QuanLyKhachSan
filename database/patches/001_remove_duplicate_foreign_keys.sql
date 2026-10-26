-- ============================================================================
-- Patch 001: Xoa cac foreign key trung do schema baseline bi chay lai
-- Database doi chieu: HotelManagementDB_LocalSimple
-- Ngay xac minh: 2026-10-26
--
-- Patch chi xoa 26 constraint tao nham luc 2026-10-26 11:47:41.
-- Moi quan he van con constraint goc, da trusted va dang hoat dong.
-- Script co the chay lai: constraint khong con se duoc bo qua.
-- ============================================================================

SET NOCOUNT ON;
SET XACT_ABORT ON;

BEGIN TRY
    BEGIN TRANSACTION;

    IF OBJECT_ID(N'dbo.FK__BaoTri__MaPhong__5F7E2DAC', N'F') IS NOT NULL
        ALTER TABLE dbo.BaoTri DROP CONSTRAINT FK__BaoTri__MaPhong__5F7E2DAC;
    IF OBJECT_ID(N'dbo.FK__ChiTietPh__MaPho__607251E5', N'F') IS NOT NULL
        ALTER TABLE dbo.ChiTietPhong DROP CONSTRAINT FK__ChiTietPh__MaPho__607251E5;
    IF OBJECT_ID(N'dbo.FK__ChuyenPho__MaDat__6166761E', N'F') IS NOT NULL
        ALTER TABLE dbo.ChuyenPhong DROP CONSTRAINT FK__ChuyenPho__MaDat__6166761E;
    IF OBJECT_ID(N'dbo.FK__ChuyenPho__MaDat__625A9A57', N'F') IS NOT NULL
        ALTER TABLE dbo.ChuyenPhong DROP CONSTRAINT FK__ChuyenPho__MaDat__625A9A57;
    IF OBJECT_ID(N'dbo.FK__DatPhong__MaKhac__634EBE90', N'F') IS NOT NULL
        ALTER TABLE dbo.DatPhong DROP CONSTRAINT FK__DatPhong__MaKhac__634EBE90;
    IF OBJECT_ID(N'dbo.FK__DatPhong__MaLuot__6442E2C9', N'F') IS NOT NULL
        ALTER TABLE dbo.DatPhong DROP CONSTRAINT FK__DatPhong__MaLuot__6442E2C9;
    IF OBJECT_ID(N'dbo.FK__DatPhong__MaPhon__65370702', N'F') IS NOT NULL
        ALTER TABLE dbo.DatPhong DROP CONSTRAINT FK__DatPhong__MaPhon__65370702;
    IF OBJECT_ID(N'dbo.FK__LuotLuuTr__MaKha__662B2B3B', N'F') IS NOT NULL
        ALTER TABLE dbo.LuotLuuTru DROP CONSTRAINT FK__LuotLuuTr__MaKha__662B2B3B;
    IF OBJECT_ID(N'dbo.FK__Phong__MaLoaiPho__671F4F74', N'F') IS NOT NULL
        ALTER TABLE dbo.Phong DROP CONSTRAINT FK__Phong__MaLoaiPho__671F4F74;
    IF OBJECT_ID(N'dbo.FK__Phong__MaTang__681373AD', N'F') IS NOT NULL
        ALTER TABLE dbo.Phong DROP CONSTRAINT FK__Phong__MaTang__681373AD;
    IF OBJECT_ID(N'dbo.FK__PhongDanh__MaDat__690797E6', N'F') IS NOT NULL
        ALTER TABLE dbo.PhongDanhGiaNhan DROP CONSTRAINT FK__PhongDanh__MaDat__690797E6;
    IF OBJECT_ID(N'dbo.FK__PhongDanh__MaKha__69FBBC1F', N'F') IS NOT NULL
        ALTER TABLE dbo.PhongDanhGiaNhan DROP CONSTRAINT FK__PhongDanh__MaKha__69FBBC1F;
    IF OBJECT_ID(N'dbo.FK__PhongDanh__MaPho__6AEFE058', N'F') IS NOT NULL
        ALTER TABLE dbo.PhongDanhGiaNhan DROP CONSTRAINT FK__PhongDanh__MaPho__6AEFE058;
    IF OBJECT_ID(N'dbo.FK__PhongLuuT__MaDat__6BE40491', N'F') IS NOT NULL
        ALTER TABLE dbo.PhongLuuTruNhan DROP CONSTRAINT FK__PhongLuuT__MaDat__6BE40491;
    IF OBJECT_ID(N'dbo.FK__PhongLuuT__MaKha__6CD828CA', N'F') IS NOT NULL
        ALTER TABLE dbo.PhongLuuTruNhan DROP CONSTRAINT FK__PhongLuuT__MaKha__6CD828CA;
    IF OBJECT_ID(N'dbo.FK__PhongLuuT__MaPho__6DCC4D03', N'F') IS NOT NULL
        ALTER TABLE dbo.PhongLuuTruNhan DROP CONSTRAINT FK__PhongLuuT__MaPho__6DCC4D03;
    IF OBJECT_ID(N'dbo.FK__PhongPhan__MaDat__6EC0713C', N'F') IS NOT NULL
        ALTER TABLE dbo.PhongPhanAnhNhan DROP CONSTRAINT FK__PhongPhan__MaDat__6EC0713C;
    IF OBJECT_ID(N'dbo.FK__PhongPhan__MaKha__6FB49575', N'F') IS NOT NULL
        ALTER TABLE dbo.PhongPhanAnhNhan DROP CONSTRAINT FK__PhongPhan__MaKha__6FB49575;
    IF OBJECT_ID(N'dbo.FK__PhongPhan__MaPho__70A8B9AE', N'F') IS NOT NULL
        ALTER TABLE dbo.PhongPhanAnhNhan DROP CONSTRAINT FK__PhongPhan__MaPho__70A8B9AE;
    IF OBJECT_ID(N'dbo.FK__PhongTien__MaPho__719CDDE7', N'F') IS NOT NULL
        ALTER TABLE dbo.PhongTienNghi DROP CONSTRAINT FK__PhongTien__MaPho__719CDDE7;
    IF OBJECT_ID(N'dbo.FK__SuDungDic__MaDat__72910220', N'F') IS NOT NULL
        ALTER TABLE dbo.SuDungDichVu DROP CONSTRAINT FK__SuDungDic__MaDat__72910220;
    IF OBJECT_ID(N'dbo.FK__SuDungDic__MaDic__73852659', N'F') IS NOT NULL
        ALTER TABLE dbo.SuDungDichVu DROP CONSTRAINT FK__SuDungDic__MaDic__73852659;
    IF OBJECT_ID(N'dbo.FK__VeSinh__MaPhong__74794A92', N'F') IS NOT NULL
        ALTER TABLE dbo.VeSinh DROP CONSTRAINT FK__VeSinh__MaPhong__74794A92;
    IF OBJECT_ID(N'dbo.FK__YeuCauDat__MaDat__756D6ECB', N'F') IS NOT NULL
        ALTER TABLE dbo.YeuCauDatPhong DROP CONSTRAINT FK__YeuCauDat__MaDat__756D6ECB;
    IF OBJECT_ID(N'dbo.FK__YeuCauDat__MaKha__76619304', N'F') IS NOT NULL
        ALTER TABLE dbo.YeuCauDatPhong DROP CONSTRAINT FK__YeuCauDat__MaKha__76619304;
    IF OBJECT_ID(N'dbo.FK__YeuCauDat__MaPho__7755B73D', N'F') IS NOT NULL
        ALTER TABLE dbo.YeuCauDatPhong DROP CONSTRAINT FK__YeuCauDat__MaPho__7755B73D;

    IF EXISTS (
        SELECT 1
        FROM sys.foreign_keys fk
        JOIN sys.foreign_key_columns fkc
          ON fkc.constraint_object_id = fk.object_id
        GROUP BY
            fk.parent_object_id,
            fk.referenced_object_id,
            fkc.parent_column_id,
            fkc.referenced_column_id,
            fk.delete_referential_action,
            fk.update_referential_action
        HAVING COUNT(*) > 1
    )
        THROW 51001, N'Van con foreign key trung sau khi don dep. Transaction da duoc rollback.', 1;

    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF XACT_STATE() <> 0
        ROLLBACK TRANSACTION;
    THROW;
END CATCH;
GO

SELECT COUNT(*) AS TotalForeignKeysAfterCleanup
FROM sys.foreign_keys;
GO
