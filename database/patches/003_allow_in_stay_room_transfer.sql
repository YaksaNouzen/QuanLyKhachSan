-- ============================================================================
-- Patch 003: Cho phep doi phong khi don dang GIU_PHONG hoac DANG_O
-- Gia chot cua don moi lay theo phong moi.
-- ============================================================================

CREATE OR ALTER PROCEDURE dbo.sp_DatPhong_DoiPhong
    @MaDatPhongCu    VARCHAR(MAX),
    @MaPhongMoi      VARCHAR(MAX),
    @LyDo            NVARCHAR(MAX),
    @NguoiThucHien   NVARCHAR(MAX) = NULL,
    @MaDatPhongMoi   VARCHAR(MAX) = NULL,
    @MaChuyenPhong   VARCHAR(MAX) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

    BEGIN TRY
        IF @MaDatPhongCu IS NULL
           OR DATALENGTH(TRIM(@MaDatPhongCu)) NOT BETWEEN 1 AND 20
            THROW 50511, N'Mã đơn cũ không hợp lệ.', 1;
        IF @MaDatPhongMoi IS NULL
           OR DATALENGTH(TRIM(@MaDatPhongMoi)) NOT BETWEEN 1 AND 20
            THROW 50512, N'Mã đơn mới không hợp lệ.', 1;
        IF @MaPhongMoi IS NULL
           OR DATALENGTH(TRIM(@MaPhongMoi)) NOT BETWEEN 1 AND 20
            THROW 50513, N'Mã phòng mới không hợp lệ.', 1;
        IF @MaChuyenPhong IS NULL
           OR DATALENGTH(TRIM(@MaChuyenPhong)) NOT BETWEEN 1 AND 20
            THROW 50514, N'Mã chuyển phòng không hợp lệ.', 1;
        IF @LyDo IS NULL
           OR DATALENGTH(TRIM(@LyDo)) = 0
           OR DATALENGTH(TRIM(@LyDo)) > 2000
            THROW 50501, N'Lý do chuyển phòng không hợp lệ.', 1;
        IF @NguoiThucHien IS NOT NULL
           AND DATALENGTH(TRIM(@NguoiThucHien)) > 200
            THROW 50515, N'Tên người thực hiện quá dài.', 1;

        DECLARE
            @MaCu VARCHAR(20) = TRIM(@MaDatPhongCu),
            @MaMoi VARCHAR(20) = TRIM(@MaDatPhongMoi),
            @PhongMoi VARCHAR(20) = TRIM(@MaPhongMoi),
            @MaChuyen VARCHAR(20) = TRIM(@MaChuyenPhong),
            @LyDoChuan NVARCHAR(1000) = TRIM(@LyDo),
            @NguoiThucHienChuan NVARCHAR(100) =
                NULLIF(TRIM(@NguoiThucHien), N''),
            @ThoiDiemChuyen DATETIME2(0) =
                CONVERT(DATETIME2(0), SYSDATETIME());

        IF @MaCu = @MaMoi
            THROW 50503, N'Mã đơn mới phải khác mã đơn cũ.', 1;

        BEGIN TRANSACTION;

        DECLARE
            @MaKH VARCHAR(20),
            @PhongCu VARCHAR(20),
            @Nhan DATETIME2(0),
            @Tra DATETIME2(0),
            @NgayNhanThucTeCu DATETIME2(0),
            @TrangThaiCu VARCHAR(20),
            @MaLT VARCHAR(20),
            @KhoangBatDau DATETIME2(0),
            @TrangThaiMoi VARCHAR(20),
            @NgayNhanThucTeMoi DATETIME2(0);

        SELECT
            @MaKH = MaKhachHang,
            @PhongCu = MaPhong,
            @Nhan = NgayNhanDuKien,
            @Tra = NgayTraDuKien,
            @NgayNhanThucTeCu = NgayNhan,
            @TrangThaiCu = TrangThai,
            @MaLT = MaLuotLuuTru
        FROM dbo.DatPhong WITH (UPDLOCK, HOLDLOCK)
        WHERE MaDatPhong = @MaCu;

        IF @TrangThaiCu IS NULL
            THROW 50505, N'Đơn đặt phòng cũ không tồn tại.', 1;
        IF @TrangThaiCu NOT IN ('GIU_PHONG', 'DANG_O')
            THROW 50506,
                N'Chỉ được đổi phòng cho đơn đang giữ phòng hoặc đang lưu trú.', 1;
        IF @TrangThaiCu = 'DANG_O' AND @NgayNhanThucTeCu IS NULL
            THROW 50520, N'Đơn đang ở nhưng thiếu thời điểm nhận thực tế.', 1;
        IF @PhongCu = @PhongMoi
            THROW 50507, N'Phòng mới trùng với phòng cũ.', 1;

        SET @KhoangBatDau = CASE
            WHEN @TrangThaiCu = 'DANG_O' THEN @ThoiDiemChuyen
            ELSE @Nhan
        END;
        SET @TrangThaiMoi = @TrangThaiCu;
        SET @NgayNhanThucTeMoi = CASE
            WHEN @TrangThaiCu = 'DANG_O' THEN @ThoiDiemChuyen
            ELSE NULL
        END;

        IF @Tra <= @KhoangBatDau
            THROW 50518,
                N'Đơn đã quá hạn trả. Hãy gia hạn trước khi đổi phòng.', 1;

        IF EXISTS (
            SELECT 1 FROM dbo.MaDatPhongDaCap WITH (UPDLOCK, HOLDLOCK)
            WHERE MaDatPhong = @MaMoi
        ) OR EXISTS (
            SELECT 1 FROM dbo.DatPhong WHERE MaDatPhong = @MaMoi
        )
            THROW 50504, N'Mã đơn mới đã được sử dụng.', 1;

        IF EXISTS (
            SELECT 1 FROM dbo.ChuyenPhong WITH (UPDLOCK, HOLDLOCK)
            WHERE MaChuyenPhong = @MaChuyen
        )
            THROW 50516, N'Mã chuyển phòng đã tồn tại.', 1;

        DECLARE
            @TrangThaiPhong VARCHAR(20),
            @NgungSuDung BIT,
            @GiaPhongMoi DECIMAL(18,2);

        SELECT
            @TrangThaiPhong = p.TrangThai,
            @NgungSuDung = p.NgungSuDung,
            @GiaPhongMoi = COALESCE(p.GiaPhong, lp.GiaTheoNgay)
        FROM dbo.Phong p WITH (UPDLOCK, HOLDLOCK)
        JOIN dbo.LoaiPhong lp ON lp.MaLoaiPhong = p.MaLoaiPhong
        WHERE p.MaPhong = @PhongMoi;

        IF @TrangThaiPhong IS NULL
            THROW 50508, N'Phòng mới không tồn tại.', 1;
        IF @NgungSuDung = 1 OR @TrangThaiPhong <> 'TRONG'
            THROW 50509, N'Phòng mới không sẵn sàng.', 1;
        IF @GiaPhongMoi IS NULL OR @GiaPhongMoi <= 0
            THROW 50519, N'Phòng mới chưa có giá hợp lệ.', 1;

        IF EXISTS (
            SELECT 1
            FROM dbo.DatPhong
            WHERE MaPhong = @PhongMoi
              AND (
                  TrangThai = 'DANG_O'
                  OR (
                      TrangThai = 'GIU_PHONG'
                      AND @KhoangBatDau < NgayTraDuKien
                      AND @Tra > NgayNhanDuKien
                  )
              )
        )
            THROW 50510,
                N'Phòng mới đang có khách hoặc đã được đặt trong khoảng cần chuyển.', 1;

        INSERT INTO dbo.MaDatPhongDaCap (MaDatPhong)
        VALUES (@MaMoi);

        INSERT INTO dbo.DatPhong
        (
            MaDatPhong,
            MaKhachHang,
            MaPhong,
            GiaPhongChot,
            NgayNhan,
            NgayNhanDuKien,
            NgayTraDuKien,
            TrangThai,
            MaLuotLuuTru
        )
        VALUES
        (
            @MaMoi,
            @MaKH,
            @PhongMoi,
            @GiaPhongMoi,
            @NgayNhanThucTeMoi,
            @KhoangBatDau,
            @Tra,
            @TrangThaiMoi,
            @MaLT
        );

        INSERT INTO dbo.ChuyenPhong
        (
            MaChuyenPhong,
            MaDatPhongCu,
            MaDatPhongMoi,
            LyDo,
            NguoiThucHien,
            ThoiGian
        )
        VALUES
        (
            @MaChuyen,
            @MaCu,
            @MaMoi,
            @LyDoChuan,
            @NguoiThucHienChuan,
            @ThoiDiemChuyen
        );

        UPDATE dbo.DatPhong
        SET
            TrangThai = CASE
                WHEN @TrangThaiCu = 'DANG_O' THEN 'DA_TRA'
                ELSE 'DA_HUY'
            END,
            NgayTra = CASE
                WHEN @TrangThaiCu = 'DANG_O' THEN @ThoiDiemChuyen
                ELSE NgayTra
            END
        WHERE MaDatPhong = @MaCu
          AND TrangThai = @TrangThaiCu;

        IF @@ROWCOUNT <> 1
            THROW 50517, N'Không thể kết thúc đơn cũ khi đổi phòng.', 1;

        COMMIT TRANSACTION;

        SELECT
            N'DOI_PHONG_THANH_CONG' AS KetQua,
            @MaChuyen AS MaChuyenPhong,
            @MaCu AS MaDatPhongCu,
            @MaMoi AS MaDatPhongMoi,
            @PhongMoi AS MaPhongMoi,
            @GiaPhongMoi AS GiaPhongChotMoi,
            @TrangThaiMoi AS TrangThaiMoi,
            @ThoiDiemChuyen AS ThoiDiemChuyen;
    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0
            ROLLBACK TRANSACTION;
        THROW;
    END CATCH;
END;
GO
