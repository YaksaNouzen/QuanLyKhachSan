/*
  Dữ liệu khởi đầu cho 70 phòng, chạy thủ công trong SSMS sau FULL_DATABASE.sql.
  T2-T4: 14 STD/tầng; T5-T7: 8 DLX/tầng; T8: 4 PRS.
  Chạy lại không tạo trùng; không sửa/xóa dữ liệu có sẵn.
*/
USE QuanLyKhachSan;
GO
SET NOCOUNT ON;
SET XACT_ABORT ON;

-- Giá niêm yết theo đêm và số người tối đa đã thống nhất.
DECLARE @GiaSTD DECIMAL(18,2) = 1000000, @SucChuaSTD INT = 2;
DECLARE @GiaDLX DECIMAL(18,2) = 7000000, @SucChuaDLX INT = 4;
DECLARE @GiaPRS DECIMAL(18,2) = 15000000, @SucChuaPRS INT = 7;

IF @GiaSTD IS NULL OR @GiaDLX IS NULL OR @GiaPRS IS NULL
   OR @SucChuaSTD IS NULL OR @SucChuaDLX IS NULL OR @SucChuaPRS IS NULL
    THROW 52000, N'Chưa điền giá và sức chứa cho STD, DLX, PRS.', 1;
IF @GiaSTD <= 0 OR @GiaDLX <= 0 OR @GiaPRS <= 0
   OR @SucChuaSTD <= 0 OR @SucChuaDLX <= 0 OR @SucChuaPRS <= 0
    THROW 52001, N'Giá và sức chứa phải lớn hơn 0.', 1;

DECLARE @Rooms TABLE (
    SoPhong VARCHAR(20) NOT NULL PRIMARY KEY,
    MaTang VARCHAR(20) NOT NULL,
    MaLoaiPhong VARCHAR(20) NOT NULL
);
DECLARE @Tang INT = 2, @So INT, @GioiHan INT, @Loai VARCHAR(20);
WHILE @Tang <= 8
BEGIN
    SET @Loai = CASE WHEN @Tang <= 4 THEN 'STD'
                     WHEN @Tang <= 7 THEN 'DLX' ELSE 'PRS' END;
    SET @GioiHan = CASE WHEN @Tang <= 4 THEN 14
                        WHEN @Tang <= 7 THEN 8 ELSE 4 END;
    SET @So = 1;
    WHILE @So <= @GioiHan
    BEGIN
        INSERT INTO @Rooms (SoPhong, MaTang, MaLoaiPhong)
        VALUES (CONCAT(@Tang, RIGHT(CONCAT('0', @So), 2)),
                CONCAT('T', @Tang), @Loai);
        SET @So += 1;
    END;
    SET @Tang += 1;
END;
IF (SELECT COUNT(*) FROM @Rooms) <> 70
    THROW 52002, N'Số phòng dự kiến không bằng 70.', 1;

BEGIN TRY
    BEGIN TRANSACTION;
    IF OBJECT_ID(N'dbo.Phong', N'U') IS NULL
       OR OBJECT_ID(N'dbo.LoaiPhong', N'U') IS NULL
       OR OBJECT_ID(N'dbo.Tang', N'U') IS NULL
       OR OBJECT_ID(N'dbo.sp_LayMaTuDong', N'P') IS NULL
        THROW 52003, N'Chưa có schema QuanLyKhachSan đầy đủ.', 1;

    DECLARE @MaTang VARCHAR(20), @TenTang NVARCHAR(100);
    SET @Tang = 2;
    WHILE @Tang <= 8
    BEGIN
        SET @MaTang = CONCAT('T', @Tang);
        SET @TenTang = CONCAT(N'Tầng ', @Tang);
        IF NOT EXISTS (SELECT 1 FROM dbo.Tang WHERE MaTang = @MaTang)
            INSERT INTO dbo.Tang (MaTang, TenTang) VALUES (@MaTang, @TenTang);
        SET @Tang += 1;
    END;

    -- Dùng lại hạng đã có (kể cả mã STD001 thay cho STD), không đổi tên/giá cũ.
    DECLARE @MaSTD VARCHAR(20) = COALESCE(
        (SELECT MaLoaiPhong FROM dbo.LoaiPhong WHERE MaLoaiPhong = 'STD'),
        (SELECT MaLoaiPhong FROM dbo.LoaiPhong WHERE TenLoai = N'Tiêu chuẩn'), 'STD');
    DECLARE @MaDLX VARCHAR(20) = COALESCE(
        (SELECT MaLoaiPhong FROM dbo.LoaiPhong WHERE MaLoaiPhong = 'DLX'),
        (SELECT MaLoaiPhong FROM dbo.LoaiPhong WHERE TenLoai = N'Cao cấp'), 'DLX');
    DECLARE @MaPRS VARCHAR(20) = COALESCE(
        (SELECT MaLoaiPhong FROM dbo.LoaiPhong WHERE MaLoaiPhong = 'PRS'),
        (SELECT MaLoaiPhong FROM dbo.LoaiPhong
         WHERE TenLoai IN (N'Tổng thống', N'Phòng tổng thống')), 'PRS');

    DECLARE @Types TABLE (MaLoaiPhong VARCHAR(20), TenLoai NVARCHAR(100),
                          GiaTheoNgay DECIMAL(18,2), SoNguoiToiDa INT);
    INSERT INTO @Types VALUES
        (@MaSTD, N'Tiêu chuẩn', @GiaSTD, @SucChuaSTD),
        (@MaDLX, N'Cao cấp', @GiaDLX, @SucChuaDLX),
        (@MaPRS, N'Tổng thống', @GiaPRS, @SucChuaPRS);
    IF EXISTS (
        SELECT 1 FROM @Types t JOIN dbo.LoaiPhong lp ON lp.MaLoaiPhong = t.MaLoaiPhong
        WHERE lp.GiaTheoNgay <> t.GiaTheoNgay OR lp.SoNguoiToiDa <> t.SoNguoiToiDa
    ) THROW 52004, N'Hạng phòng đã tồn tại nhưng giá hoặc sức chứa khác; không tự ghi đè.', 1;
    INSERT INTO dbo.LoaiPhong (MaLoaiPhong, TenLoai, GiaTheoNgay, SoNguoiToiDa)
    SELECT t.MaLoaiPhong, t.TenLoai, t.GiaTheoNgay, t.SoNguoiToiDa
    FROM @Types t WHERE NOT EXISTS (
        SELECT 1 FROM dbo.LoaiPhong lp WHERE lp.MaLoaiPhong = t.MaLoaiPhong);

    UPDATE @Rooms SET MaLoaiPhong = @MaSTD WHERE MaLoaiPhong = 'STD';
    UPDATE @Rooms SET MaLoaiPhong = @MaDLX WHERE MaLoaiPhong = 'DLX';
    UPDATE @Rooms SET MaLoaiPhong = @MaPRS WHERE MaLoaiPhong = 'PRS';

    IF EXISTS (
        SELECT 1 FROM @Rooms r JOIN dbo.Phong p ON p.SoPhong = r.SoPhong
        WHERE p.MaTang <> r.MaTang OR p.MaLoaiPhong <> r.MaLoaiPhong
    ) THROW 52005, N'Số phòng đã tồn tại với tầng hoặc hạng khác; không tự ghi đè.', 1;

    DECLARE @SoPhong VARCHAR(20), @MaLoai VARCHAR(20), @MaPhong VARCHAR(20);
    DECLARE @Generated TABLE (MaMoi VARCHAR(20));
    DECLARE room_cursor CURSOR LOCAL FAST_FORWARD FOR
        SELECT SoPhong, MaTang, MaLoaiPhong FROM @Rooms ORDER BY SoPhong;
    OPEN room_cursor;
    FETCH NEXT FROM room_cursor INTO @SoPhong, @MaTang, @MaLoai;
    WHILE @@FETCH_STATUS = 0
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM dbo.Phong WHERE SoPhong = @SoPhong)
        BEGIN
            SET @MaPhong = NULL;
            WHILE @MaPhong IS NULL OR EXISTS (
                SELECT 1 FROM dbo.Phong WHERE MaPhong = @MaPhong)
            BEGIN
                DELETE FROM @Generated;
                INSERT INTO @Generated EXEC dbo.sp_LayMaTuDong @LoaiMa = 'PHONG';
                SELECT @MaPhong = MaMoi FROM @Generated;
            END;
            INSERT INTO dbo.Phong
                (MaPhong, SoPhong, MaLoaiPhong, MaTang, GiaPhong, TrangThai, NgungSuDung)
            VALUES (@MaPhong, @SoPhong, @MaLoai, @MaTang, NULL, 'TRONG', 0);
        END;
        FETCH NEXT FROM room_cursor INTO @SoPhong, @MaTang, @MaLoai;
    END;
    CLOSE room_cursor;
    DEALLOCATE room_cursor;
    COMMIT TRANSACTION;
END TRY
BEGIN CATCH
    IF CURSOR_STATUS('local', 'room_cursor') >= 0 CLOSE room_cursor;
    IF CURSOR_STATUS('local', 'room_cursor') > -3 DEALLOCATE room_cursor;
    IF @@TRANCOUNT > 0 ROLLBACK TRANSACTION;
    THROW;
END CATCH;

SELECT p.MaPhong, p.SoPhong, p.MaTang, p.MaLoaiPhong,
       COALESCE(p.GiaPhong, lp.GiaTheoNgay) AS GiaMoiDem, p.TrangThai
FROM @Rooms r JOIN dbo.Phong p ON p.SoPhong = r.SoPhong
JOIN dbo.LoaiPhong lp ON lp.MaLoaiPhong = p.MaLoaiPhong
ORDER BY TRY_CONVERT(INT, p.SoPhong);
SELECT COUNT(*) AS SoPhongDaCo FROM @Rooms r
JOIN dbo.Phong p ON p.SoPhong = r.SoPhong;
