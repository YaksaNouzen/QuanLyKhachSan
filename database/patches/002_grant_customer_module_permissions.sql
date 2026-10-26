-- ============================================================================
-- Patch 002: Quyen toi thieu cho module Quan ly khach hang
-- Database doi chieu: HotelManagementDB_LocalSimple
--
-- hotelbooking_app da co db_datareader va EXECUTE cac procedure DatPhong.
-- Patch chi bo sung quyen sinh ma, them va cap nhat ho so khach hang.
-- Khong cap DELETE, db_datawriter hoac quyen quan tri.
-- ============================================================================

SET NOCOUNT ON;
SET XACT_ABORT ON;

IF USER_ID(N'hotelbooking_app') IS NULL
    THROW 51002, N'Khong tim thay database user hotelbooking_app.', 1;

GRANT UPDATE ON OBJECT::dbo.SeqKhachHang TO [hotelbooking_app];
GRANT INSERT, UPDATE ON OBJECT::dbo.KhachHang TO [hotelbooking_app];
GO

EXECUTE AS USER = N'hotelbooking_app';
SELECT
    HAS_PERMS_BY_NAME(N'dbo.SeqKhachHang', N'OBJECT', N'UPDATE') AS SequenceUpdate,
    HAS_PERMS_BY_NAME(N'dbo.KhachHang', N'OBJECT', N'INSERT') AS CustomerInsert,
    HAS_PERMS_BY_NAME(N'dbo.KhachHang', N'OBJECT', N'UPDATE') AS CustomerUpdate;
REVERT;
GO
