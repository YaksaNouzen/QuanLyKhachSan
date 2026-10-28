-- Patch N2_002: cap quyen ghi toi thieu cho module phong nhom 2.
-- Can co database user hotelbooking_app va quyen db_datareader tu cau hinh chung.
-- Patch 001 phai chay truoc de tao cac cot nghiep vu.

SET NOCOUNT ON;

IF USER_ID(N'hotelbooking_app') IS NULL
    THROW 51002, N'Khong tim thay database user hotelbooking_app.', 1;

GRANT INSERT, UPDATE ON OBJECT::dbo.Phong TO [hotelbooking_app];
GRANT INSERT, UPDATE, DELETE ON OBJECT::dbo.LoaiPhong TO [hotelbooking_app];
GRANT INSERT, UPDATE, DELETE ON OBJECT::dbo.Tang TO [hotelbooking_app];
GRANT INSERT, UPDATE ON OBJECT::dbo.VeSinh TO [hotelbooking_app];
GRANT INSERT, UPDATE ON OBJECT::dbo.BaoTri TO [hotelbooking_app];
GO

EXECUTE AS USER = N'hotelbooking_app';
SELECT
    HAS_PERMS_BY_NAME(N'dbo.Phong', N'OBJECT', N'UPDATE') AS RoomUpdate,
    HAS_PERMS_BY_NAME(N'dbo.Phong', N'OBJECT', N'INSERT') AS RoomInsert,
    HAS_PERMS_BY_NAME(N'dbo.LoaiPhong', N'OBJECT', N'DELETE') AS RoomTypeDelete,
    HAS_PERMS_BY_NAME(N'dbo.Tang', N'OBJECT', N'DELETE') AS FloorDelete,
    HAS_PERMS_BY_NAME(N'dbo.VeSinh', N'OBJECT', N'INSERT') AS HousekeepingInsert,
    HAS_PERMS_BY_NAME(N'dbo.BaoTri', N'OBJECT', N'UPDATE') AS MaintenanceUpdate;
REVERT;
GO
