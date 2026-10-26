-- ============================================================================
-- HOTEL MANAGEMENT DATABASE - SCHEMA BASELINE
-- SQL Server 2022+
-- Verified against HotelManagementDB_LocalSimple on 2026-10-26
--
-- Schema only: no production data, login, database user, or password.
-- Run this script while connected to an empty target database.
-- ============================================================================

IF OBJECT_ID(N'dbo.DatPhong', N'U') IS NOT NULL
   OR OBJECT_ID(N'dbo.KhachHang', N'U') IS NOT NULL
   OR OBJECT_ID(N'dbo.Phong', N'U') IS NOT NULL
   OR OBJECT_ID(N'dbo.SeqKhachHang', N'SO') IS NOT NULL
BEGIN
    RAISERROR(
        N'schema.sql chi dung de khoi tao database trong. Database hien tai da co schema; hay giu nguyen va chi chay patch moi trong database/patches/.',
        16,
        1
    );
    SET NOEXEC ON;
END;
GO

CREATE SEQUENCE [dbo].[SeqKhachHang] 
 AS [bigint]
 START WITH 1
 INCREMENT BY 1
 MINVALUE -9223372036854775808
 MAXVALUE 9223372036854775807
 CACHE 
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[BaoTri](
	[MaBaoTri] [varchar](36) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaPhong] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[NoiDung] [nvarchar](500) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[NguoiPhuTrach] [nvarchar](100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[BatDau] [datetime2](0) NOT NULL,
	[KetThuc] [datetime2](0) NULL,
PRIMARY KEY CLUSTERED 
(
	[MaBaoTri] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ChiTietPhong](
	[MaPhong] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MoTa] [nvarchar](2000) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[MaPhong] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[ChuyenPhong](
	[MaChuyenPhong] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaDatPhongCu] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaDatPhongMoi] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[LyDo] [nvarchar](1000) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[NguoiThucHien] [nvarchar](100) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[ThoiGian] [datetime2](0) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[MaChuyenPhong] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DatPhong](
	[MaDatPhong] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaKhachHang] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaPhong] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[GiaPhongChot] [decimal](18, 2) NOT NULL,
	[NgayDat] [datetime2](0) NOT NULL,
	[NgayNhan] [datetime2](0) NULL,
	[NgayTra] [datetime2](0) NULL,
	[TrangThai] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaLuotLuuTru] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[NgayNhanDuKien] [datetime2](0) NOT NULL,
	[NgayTraDuKien] [datetime2](0) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[MaDatPhong] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[DichVu](
	[MaDichVu] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[TenDichVu] [nvarchar](100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[DonGia] [decimal](18, 2) NOT NULL,
	[DonViTinh] [nvarchar](30) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[NgungSuDung] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[MaDichVu] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[KhachHang](
	[MaKhachHang] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[HoTen] [nvarchar](100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[LoaiGiayTo] [varchar](10) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[SoGiayTo] [varchar](30) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[QuocGiaCap] [char](2) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[SoDienThoai] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[Email] [nvarchar](254) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[NgaySinh] [date] NULL,
	[GioiTinh] [varchar](15) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[QuocTich] [char](2) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[DiaChi] [nvarchar](255) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[NgayTao] [datetime2](0) NOT NULL,
	[TrangThai] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[MaKhachHang] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[LoaiPhong](
	[MaLoaiPhong] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[TenLoai] [nvarchar](100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[GiaTheoNgay] [decimal](18, 2) NOT NULL,
	[SoNguoiToiDa] [int] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[MaLoaiPhong] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[LuotLuuTru](
	[MaLuotLuuTru] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaKhachHang] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[TaoLuc] [datetime2](0) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[MaLuotLuuTru] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[MaDatPhongDaCap](
	[MaDatPhong] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[ThoiGianDangKy] [datetime2](0) NOT NULL,
 CONSTRAINT [PK_MaDatPhongDaCap] PRIMARY KEY CLUSTERED 
(
	[MaDatPhong] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Phong](
	[MaPhong] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[SoPhong] [nvarchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaLoaiPhong] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaTang] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[GiaPhong] [decimal](18, 2) NULL,
	[TrangThai] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[NgungSuDung] [bit] NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[MaPhong] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PhongDanhGiaNhan](
	[MaDanhGia] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaDatPhong] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaPhong] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaKhachHang] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[Diem] [int] NOT NULL,
	[NoiDung] [nvarchar](2000) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[TaoLuc] [datetime2](0) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[MaDanhGia] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PhongLuuTruNhan](
	[MaDatPhong] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaPhong] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaKhachHang] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[TrangThai] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[PhienBan] [bigint] NOT NULL,
	[CapNhatLuc] [datetime2](0) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[MaDatPhong] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PhongPhanAnhNhan](
	[MaPhanAnh] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaDatPhong] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaPhong] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaKhachHang] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[NoiDung] [nvarchar](2000) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[TrangThai] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[PhienBan] [bigint] NOT NULL,
	[CapNhatLuc] [datetime2](0) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[MaPhanAnh] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[PhongTienNghi](
	[MaPhong] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaTienNghi] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[TenTienNghi] [nvarchar](100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[SoLuong] [int] NOT NULL,
	[TinhTrang] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[MaPhong] ASC,
	[MaTienNghi] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[SuDungDichVu](
	[MaSuDung] [varchar](36) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaDatPhong] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaDichVu] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[SoLuong] [int] NOT NULL,
	[DonGia] [decimal](18, 2) NOT NULL,
	[ThoiGianSuDung] [datetime2](0) NOT NULL,
	[ThanhTien]  AS (CONVERT([decimal](18,2),[SoLuong]*[DonGia])),
PRIMARY KEY CLUSTERED 
(
	[MaSuDung] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[Tang](
	[MaTang] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[TenTang] [nvarchar](100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[MaTang] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[VeSinh](
	[MaVeSinh] [varchar](36) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaPhong] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[NoiDung] [nvarchar](500) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[NguoiPhuTrach] [nvarchar](100) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[BatDau] [datetime2](0) NOT NULL,
	[KetThuc] [datetime2](0) NULL,
PRIMARY KEY CLUSTERED 
(
	[MaVeSinh] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[YeuCauDatPhong](
	[MaYeuCau] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaKhachHang] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaPhong] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[GhiChu] [nvarchar](1000) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[TrangThai] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[MaDatPhong] [varchar](20) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
	[LyDo] [nvarchar](1000) COLLATE SQL_Latin1_General_CP1_CI_AS NOT NULL,
	[PhienBan] [bigint] NOT NULL,
	[TaoLuc] [datetime2](0) NOT NULL,
	[CapNhatLuc] [datetime2](0) NOT NULL,
PRIMARY KEY CLUSTERED 
(
	[MaYeuCau] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
SET ANSI_PADDING ON
GO
CREATE UNIQUE NONCLUSTERED INDEX [UX_KhachHang_GiayTo] ON [dbo].[KhachHang]
(
	[LoaiGiayTo] ASC,
	[QuocGiaCap] ASC,
	[SoGiayTo] ASC
)
WHERE ([SoGiayTo] IS NOT NULL)
WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, SORT_IN_TEMPDB = OFF, IGNORE_DUP_KEY = OFF, DROP_EXISTING = OFF, ONLINE = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
GO
ALTER TABLE [dbo].[BaoTri] ADD  DEFAULT (sysdatetime()) FOR [BatDau]
GO
ALTER TABLE [dbo].[ChiTietPhong] ADD  DEFAULT (N'') FOR [MoTa]
GO
ALTER TABLE [dbo].[ChuyenPhong] ADD  DEFAULT (sysdatetime()) FOR [ThoiGian]
GO
ALTER TABLE [dbo].[DatPhong] ADD  DEFAULT (sysdatetime()) FOR [NgayDat]
GO
ALTER TABLE [dbo].[DatPhong] ADD  DEFAULT ('GIU_PHONG') FOR [TrangThai]
GO
ALTER TABLE [dbo].[DichVu] ADD  DEFAULT ((0)) FOR [NgungSuDung]
GO
ALTER TABLE [dbo].[KhachHang] ADD  DEFAULT ('KHONG_CUNG_CAP') FOR [GioiTinh]
GO
ALTER TABLE [dbo].[KhachHang] ADD  DEFAULT (sysdatetime()) FOR [NgayTao]
GO
ALTER TABLE [dbo].[KhachHang] ADD  DEFAULT ('HOAT_DONG') FOR [TrangThai]
GO
ALTER TABLE [dbo].[LuotLuuTru] ADD  DEFAULT (sysdatetime()) FOR [TaoLuc]
GO
ALTER TABLE [dbo].[MaDatPhongDaCap] ADD  CONSTRAINT [DF_MaDatPhongDaCap_ThoiGian]  DEFAULT (sysdatetime()) FOR [ThoiGianDangKy]
GO
ALTER TABLE [dbo].[Phong] ADD  DEFAULT ('TRONG') FOR [TrangThai]
GO
ALTER TABLE [dbo].[Phong] ADD  DEFAULT ((0)) FOR [NgungSuDung]
GO
ALTER TABLE [dbo].[PhongDanhGiaNhan] ADD  DEFAULT (sysdatetime()) FOR [TaoLuc]
GO
ALTER TABLE [dbo].[PhongLuuTruNhan] ADD  DEFAULT ((1)) FOR [PhienBan]
GO
ALTER TABLE [dbo].[PhongLuuTruNhan] ADD  DEFAULT (sysdatetime()) FOR [CapNhatLuc]
GO
ALTER TABLE [dbo].[PhongPhanAnhNhan] ADD  DEFAULT ('MOI') FOR [TrangThai]
GO
ALTER TABLE [dbo].[PhongPhanAnhNhan] ADD  DEFAULT ((1)) FOR [PhienBan]
GO
ALTER TABLE [dbo].[PhongPhanAnhNhan] ADD  DEFAULT (sysdatetime()) FOR [CapNhatLuc]
GO
ALTER TABLE [dbo].[PhongTienNghi] ADD  DEFAULT ((1)) FOR [SoLuong]
GO
ALTER TABLE [dbo].[PhongTienNghi] ADD  DEFAULT ('TOT') FOR [TinhTrang]
GO
ALTER TABLE [dbo].[SuDungDichVu] ADD  DEFAULT (sysdatetime()) FOR [ThoiGianSuDung]
GO
ALTER TABLE [dbo].[VeSinh] ADD  DEFAULT (sysdatetime()) FOR [BatDau]
GO
ALTER TABLE [dbo].[YeuCauDatPhong] ADD  DEFAULT (N'') FOR [GhiChu]
GO
ALTER TABLE [dbo].[YeuCauDatPhong] ADD  DEFAULT ('CHO_XU_LY') FOR [TrangThai]
GO
ALTER TABLE [dbo].[YeuCauDatPhong] ADD  DEFAULT (N'') FOR [LyDo]
GO
ALTER TABLE [dbo].[YeuCauDatPhong] ADD  DEFAULT ((0)) FOR [PhienBan]
GO
ALTER TABLE [dbo].[YeuCauDatPhong] ADD  DEFAULT (sysdatetime()) FOR [TaoLuc]
GO
ALTER TABLE [dbo].[YeuCauDatPhong] ADD  DEFAULT (sysdatetime()) FOR [CapNhatLuc]
GO
ALTER TABLE [dbo].[BaoTri]  WITH CHECK ADD FOREIGN KEY([MaPhong])
REFERENCES [dbo].[Phong] ([MaPhong])
GO
ALTER TABLE [dbo].[ChiTietPhong]  WITH CHECK ADD FOREIGN KEY([MaPhong])
REFERENCES [dbo].[Phong] ([MaPhong])
GO
ALTER TABLE [dbo].[ChuyenPhong]  WITH CHECK ADD FOREIGN KEY([MaDatPhongCu])
REFERENCES [dbo].[DatPhong] ([MaDatPhong])
GO
ALTER TABLE [dbo].[ChuyenPhong]  WITH CHECK ADD FOREIGN KEY([MaDatPhongMoi])
REFERENCES [dbo].[DatPhong] ([MaDatPhong])
GO
ALTER TABLE [dbo].[DatPhong]  WITH CHECK ADD FOREIGN KEY([MaKhachHang])
REFERENCES [dbo].[KhachHang] ([MaKhachHang])
GO
ALTER TABLE [dbo].[DatPhong]  WITH CHECK ADD FOREIGN KEY([MaLuotLuuTru])
REFERENCES [dbo].[LuotLuuTru] ([MaLuotLuuTru])
GO
ALTER TABLE [dbo].[DatPhong]  WITH CHECK ADD FOREIGN KEY([MaPhong])
REFERENCES [dbo].[Phong] ([MaPhong])
GO
ALTER TABLE [dbo].[LuotLuuTru]  WITH CHECK ADD FOREIGN KEY([MaKhachHang])
REFERENCES [dbo].[KhachHang] ([MaKhachHang])
GO
ALTER TABLE [dbo].[Phong]  WITH CHECK ADD FOREIGN KEY([MaLoaiPhong])
REFERENCES [dbo].[LoaiPhong] ([MaLoaiPhong])
GO
ALTER TABLE [dbo].[Phong]  WITH CHECK ADD FOREIGN KEY([MaTang])
REFERENCES [dbo].[Tang] ([MaTang])
GO
ALTER TABLE [dbo].[PhongDanhGiaNhan]  WITH CHECK ADD FOREIGN KEY([MaDatPhong])
REFERENCES [dbo].[DatPhong] ([MaDatPhong])
GO
ALTER TABLE [dbo].[PhongDanhGiaNhan]  WITH CHECK ADD FOREIGN KEY([MaKhachHang])
REFERENCES [dbo].[KhachHang] ([MaKhachHang])
GO
ALTER TABLE [dbo].[PhongDanhGiaNhan]  WITH CHECK ADD FOREIGN KEY([MaPhong])
REFERENCES [dbo].[Phong] ([MaPhong])
GO
ALTER TABLE [dbo].[PhongLuuTruNhan]  WITH CHECK ADD FOREIGN KEY([MaDatPhong])
REFERENCES [dbo].[DatPhong] ([MaDatPhong])
GO
ALTER TABLE [dbo].[PhongLuuTruNhan]  WITH CHECK ADD FOREIGN KEY([MaKhachHang])
REFERENCES [dbo].[KhachHang] ([MaKhachHang])
GO
ALTER TABLE [dbo].[PhongLuuTruNhan]  WITH CHECK ADD FOREIGN KEY([MaPhong])
REFERENCES [dbo].[Phong] ([MaPhong])
GO
ALTER TABLE [dbo].[PhongPhanAnhNhan]  WITH CHECK ADD FOREIGN KEY([MaDatPhong])
REFERENCES [dbo].[DatPhong] ([MaDatPhong])
GO
ALTER TABLE [dbo].[PhongPhanAnhNhan]  WITH CHECK ADD FOREIGN KEY([MaKhachHang])
REFERENCES [dbo].[KhachHang] ([MaKhachHang])
GO
ALTER TABLE [dbo].[PhongPhanAnhNhan]  WITH CHECK ADD FOREIGN KEY([MaPhong])
REFERENCES [dbo].[Phong] ([MaPhong])
GO
ALTER TABLE [dbo].[PhongTienNghi]  WITH CHECK ADD FOREIGN KEY([MaPhong])
REFERENCES [dbo].[Phong] ([MaPhong])
GO
ALTER TABLE [dbo].[SuDungDichVu]  WITH CHECK ADD FOREIGN KEY([MaDatPhong])
REFERENCES [dbo].[DatPhong] ([MaDatPhong])
GO
ALTER TABLE [dbo].[SuDungDichVu]  WITH CHECK ADD FOREIGN KEY([MaDichVu])
REFERENCES [dbo].[DichVu] ([MaDichVu])
GO
ALTER TABLE [dbo].[VeSinh]  WITH CHECK ADD FOREIGN KEY([MaPhong])
REFERENCES [dbo].[Phong] ([MaPhong])
GO
ALTER TABLE [dbo].[YeuCauDatPhong]  WITH CHECK ADD FOREIGN KEY([MaDatPhong])
REFERENCES [dbo].[DatPhong] ([MaDatPhong])
GO
ALTER TABLE [dbo].[YeuCauDatPhong]  WITH CHECK ADD FOREIGN KEY([MaKhachHang])
REFERENCES [dbo].[KhachHang] ([MaKhachHang])
GO
ALTER TABLE [dbo].[YeuCauDatPhong]  WITH CHECK ADD FOREIGN KEY([MaPhong])
REFERENCES [dbo].[Phong] ([MaPhong])
GO
ALTER TABLE [dbo].[DatPhong]  WITH CHECK ADD  CONSTRAINT [CK_DatPhong_ThoiGianDuKien] CHECK  (([NgayTraDuKien]>[NgayNhanDuKien]))
GO
ALTER TABLE [dbo].[DatPhong] CHECK CONSTRAINT [CK_DatPhong_ThoiGianDuKien]
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE   PROCEDURE dbo.sp_DatPhong_CapNhatLich
    @MaDatPhong VARCHAR(MAX),
    @NgayNhanDuKien DATETIME2(0),
    @NgayTraDuKien DATETIME2(0),
    @NgayNhanGoc DATETIME2(0),
    @NgayTraGoc DATETIME2(0)
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    BEGIN TRY
        IF @MaDatPhong IS NULL
           OR DATALENGTH(TRIM(@MaDatPhong)) NOT BETWEEN 1 AND 20
            THROW 50107, N'Mã đặt phòng không hợp lệ.', 1;

        DECLARE @MaDP VARCHAR(20) = TRIM(@MaDatPhong);

        IF @NgayNhanDuKien IS NULL
           OR @NgayTraDuKien IS NULL
           OR @NgayTraDuKien <= @NgayNhanDuKien
            THROW 50101,
                N'Thời gian nhận và trả dự kiến không hợp lệ.', 1;

        IF @NgayNhanGoc IS NULL OR @NgayTraGoc IS NULL
            THROW 50110,
                N'Thiếu thông tin lịch gốc để kiểm tra.', 1;

        BEGIN TRANSACTION;

        DECLARE
            @MaPhong VARCHAR(20),
            @TrangThai VARCHAR(20),
            @TrangThaiPhong VARCHAR(20),
            @NgungSuDung BIT,
            @NgayNhanHienTai DATETIME2(0),
            @NgayTraHienTai DATETIME2(0);

        SELECT
            @MaPhong = MaPhong,
            @TrangThai = TrangThai,
            @NgayNhanHienTai = NgayNhanDuKien,
            @NgayTraHienTai = NgayTraDuKien
        FROM dbo.DatPhong WITH (UPDLOCK, HOLDLOCK)
        WHERE MaDatPhong = @MaDP;

        IF @TrangThai IS NULL
            THROW 50102, N'Đơn đặt phòng không tồn tại.', 1;

        IF @TrangThai <> 'GIU_PHONG'
            THROW 50103,
                N'Chỉ được sửa lịch của đơn đang giữ phòng.', 1;

        -- Chống ghi đè lịch đã thay đổi.
        IF @NgayNhanHienTai <> @NgayNhanGoc
           OR @NgayTraHienTai <> @NgayTraGoc
            THROW 50109,
                N'Lịch đặt phòng đã thay đổi. Vui lòng tải lại dữ liệu.',
                1;

        SELECT
            @TrangThaiPhong = TrangThai,
            @NgungSuDung = NgungSuDung
        FROM dbo.Phong WITH (UPDLOCK, HOLDLOCK)
        WHERE MaPhong = @MaPhong;

        IF @TrangThaiPhong IS NULL
            THROW 50104, N'Phòng không tồn tại.', 1;

        IF @NgungSuDung = 1
           OR @TrangThaiPhong <> 'TRONG'
            THROW 50105, N'Phòng không sẵn sàng.', 1;

        IF EXISTS (
            SELECT 1
            FROM dbo.DatPhong
            WHERE MaPhong = @MaPhong
              AND MaDatPhong <> @MaDP
              AND TrangThai IN ('GIU_PHONG', 'DANG_O')
              AND @NgayNhanDuKien < NgayTraDuKien
              AND @NgayTraDuKien > NgayNhanDuKien
        )
            THROW 50106,
                N'Phòng đã có lịch đặt giao nhau.', 1;

        UPDATE dbo.DatPhong
        SET
            NgayNhanDuKien = @NgayNhanDuKien,
            NgayTraDuKien = @NgayTraDuKien
        WHERE MaDatPhong = @MaDP
          AND TrangThai = 'GIU_PHONG'
          AND NgayNhanDuKien = @NgayNhanGoc
          AND NgayTraDuKien = @NgayTraGoc;

        IF @@ROWCOUNT <> 1
            THROW 50108,
                N'Không thể cập nhật lịch đặt phòng.',
                1;

        COMMIT TRANSACTION;

        SELECT
            N'CAP_NHAT_LICH_THANH_CONG' AS KetQua,
            @MaDP AS MaDatPhong,
            @NgayNhanDuKien AS NgayNhanDuKien,
            @NgayTraDuKien AS NgayTraDuKien;

    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH;
END;
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE PROCEDURE dbo.sp_DatPhong_DoiPhong
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
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE   PROCEDURE dbo.sp_DatPhong_Huy
    @MaDatPhong VARCHAR(MAX)
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    BEGIN TRY
        IF @MaDatPhong IS NULL
           OR DATALENGTH(TRIM(@MaDatPhong)) NOT BETWEEN 1 AND 20
            THROW 50603, N'Mã đặt phòng không hợp lệ.', 1;

        DECLARE @MaDP VARCHAR(20) = TRIM(@MaDatPhong);
        DECLARE @TrangThaiDatPhong VARCHAR(20);

        BEGIN TRANSACTION;

        SELECT @TrangThaiDatPhong = TrangThai
        FROM dbo.DatPhong WITH (UPDLOCK, HOLDLOCK)
        WHERE MaDatPhong = @MaDP;

        IF @TrangThaiDatPhong IS NULL
            THROW 50601, N'Đơn đặt phòng không tồn tại.', 1;

        IF @TrangThaiDatPhong <> 'GIU_PHONG'
            THROW 50602,
                N'Chỉ đơn đang giữ phòng mới được hủy.', 1;

        UPDATE dbo.DatPhong
        SET TrangThai = 'DA_HUY'
        WHERE MaDatPhong = @MaDP
          AND TrangThai = 'GIU_PHONG';

        IF @@ROWCOUNT <> 1
            THROW 50604, N'Không thể hủy đơn đặt phòng.', 1;

        COMMIT TRANSACTION;

        SELECT
            N'HUY_DAT_PHONG_THANH_CONG' AS KetQua,
            @MaDP AS MaDatPhong,
            N'DA_HUY' AS TrangThai;

    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH;
END;
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE   PROCEDURE dbo.sp_DatPhong_NhanPhong
    @MaDatPhong VARCHAR(MAX),
    @ThoiDiemNhan DATETIME2(0) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    BEGIN TRY
        IF @MaDatPhong IS NULL
           OR DATALENGTH(TRIM(@MaDatPhong)) NOT BETWEEN 1 AND 20
            THROW 50305, N'Mã đặt phòng không hợp lệ.', 1;

        DECLARE @MaDP VARCHAR(20) = TRIM(@MaDatPhong);

        BEGIN TRANSACTION;

        DECLARE
            @TrangThai VARCHAR(20),
            @MaPhong VARCHAR(20),
            @NgayNhanDuKien DATETIME2(0),
            @TrangThaiPhong VARCHAR(20),
            @NgungSuDung BIT,
            @ThoiDiemThucTe DATETIME2(0);

        SELECT
            @TrangThai = TrangThai,
            @MaPhong = MaPhong,
            @NgayNhanDuKien = NgayNhanDuKien
        FROM dbo.DatPhong WITH (UPDLOCK, HOLDLOCK)
        WHERE MaDatPhong = @MaDP;

        IF @TrangThai IS NULL
            THROW 50301, N'Đơn đặt phòng không tồn tại.', 1;

        IF @TrangThai <> 'GIU_PHONG'
            THROW 50302,
                N'Chỉ được nhận phòng với đơn đang giữ phòng.', 1;

        -- Thời gian thực tế lấy từ SQL Server
        SET @ThoiDiemThucTe =
            CONVERT(DATETIME2(0), SYSDATETIME());

        IF @ThoiDiemThucTe < @NgayNhanDuKien
            THROW 50306,
                N'Chưa đến giờ nhận phòng dự kiến. Hãy sửa lịch trước.', 1;

        SELECT
            @TrangThaiPhong = TrangThai,
            @NgungSuDung = NgungSuDung
        FROM dbo.Phong WITH (UPDLOCK, HOLDLOCK)
        WHERE MaPhong = @MaPhong;

        IF @TrangThaiPhong IS NULL
            THROW 50303, N'Phòng không tồn tại.', 1;

        IF @NgungSuDung = 1
           OR @TrangThaiPhong <> 'TRONG'
            THROW 50304, N'Phòng không sẵn sàng để nhận.', 1;

        UPDATE dbo.DatPhong
        SET
            TrangThai = 'DANG_O',
            NgayNhan = @ThoiDiemThucTe
        WHERE MaDatPhong = @MaDP
          AND TrangThai = 'GIU_PHONG';

        IF @@ROWCOUNT <> 1
            THROW 50307, N'Không thể nhận phòng.', 1;

        COMMIT TRANSACTION;

        SELECT
            N'NHAN_PHONG_THANH_CONG' AS KetQua,
            @MaDP AS MaDatPhong,
            @ThoiDiemThucTe AS NgayNhan,
            N'DANG_O' AS TrangThai;

    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH;
END;
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE   PROCEDURE dbo.sp_DatPhong_TaoMoi
    @MaDatPhong       VARCHAR(MAX),
    @MaKhachHang      VARCHAR(MAX),
    @MaPhong          VARCHAR(MAX),
    @GiaPhongChot     DECIMAL(18,4),
    @NgayNhanDuKien   DATETIME2(0),
    @NgayTraDuKien    DATETIME2(0),
    @MaLuotLuuTru     VARCHAR(MAX) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;
    SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

    BEGIN TRY
        DECLARE @MaDP VARCHAR(20);
        DECLARE @MaKH VARCHAR(20);
        DECLARE @MaP VARCHAR(20);
        DECLARE @MaLT VARCHAR(20);
        DECLARE @Gia DECIMAL(10,2);

        -- 1. Kiểm tra mã trước khi chuyển về VARCHAR(20)
        IF @MaDatPhong IS NULL
           OR LEN(TRIM(@MaDatPhong)) NOT BETWEEN 1 AND 20
           OR DATALENGTH(TRIM(@MaDatPhong)) > 20
            THROW 50007, N'Mã đặt phòng không hợp lệ.', 1;

        IF @MaKhachHang IS NULL
           OR LEN(TRIM(@MaKhachHang)) NOT BETWEEN 1 AND 20
           OR DATALENGTH(TRIM(@MaKhachHang)) > 20
            THROW 50008, N'Mã khách hàng không hợp lệ.', 1;

        IF @MaPhong IS NULL
           OR LEN(TRIM(@MaPhong)) NOT BETWEEN 1 AND 20
           OR DATALENGTH(TRIM(@MaPhong)) > 20
            THROW 50009, N'Mã phòng không hợp lệ.', 1;

        IF @MaLuotLuuTru IS NOT NULL
           AND (
               LEN(TRIM(@MaLuotLuuTru)) NOT BETWEEN 1 AND 20
               OR DATALENGTH(TRIM(@MaLuotLuuTru)) > 20
           )
            THROW 50010, N'Mã lượt lưu trú không hợp lệ.', 1;

        SET @MaDP = TRIM(@MaDatPhong);
        SET @MaKH = TRIM(@MaKhachHang);
        SET @MaP = TRIM(@MaPhong);
        SET @MaLT = TRIM(@MaLuotLuuTru);

        -- 2. Kiểm tra giá trước khi chuyển về DECIMAL(10,2)
        IF @GiaPhongChot IS NULL
           OR @GiaPhongChot <= 0
           OR @GiaPhongChot > 99999999.99
           OR @GiaPhongChot <> ROUND(@GiaPhongChot, 2)
            THROW 50011, N'Giá phòng không hợp lệ.', 1;

        SET @Gia = CONVERT(DECIMAL(10,2), @GiaPhongChot);

        -- 3. Kiểm tra thời gian
        IF @NgayNhanDuKien IS NULL
           OR @NgayTraDuKien IS NULL
           OR @NgayTraDuKien <= @NgayNhanDuKien
            THROW 50001,
                N'Thời gian nhận và trả dự kiến không hợp lệ.', 1;

        IF @NgayNhanDuKien < CONVERT(DATETIME2(0), SYSDATETIME())
            THROW 50012,
                N'Thời gian nhận dự kiến không được ở quá khứ.', 1;

        BEGIN TRANSACTION;

        -- 4. Đăng ký mã vĩnh viễn trong cùng giao dịch
        IF EXISTS (
            SELECT 1
            FROM dbo.MaDatPhongDaCap WITH (UPDLOCK, HOLDLOCK)
            WHERE MaDatPhong = @MaDP
        )
            THROW 50002, N'Mã đặt phòng đã được sử dụng.', 1;

        IF EXISTS (
            SELECT 1
            FROM dbo.DatPhong
            WHERE MaDatPhong = @MaDP
        )
            THROW 50002, N'Mã đặt phòng đã tồn tại.', 1;

        INSERT INTO dbo.MaDatPhongDaCap (MaDatPhong)
        VALUES (@MaDP);

        -- 5. Kiểm tra khách hàng
        IF NOT EXISTS (
            SELECT 1
            FROM dbo.KhachHang
            WHERE MaKhachHang = @MaKH
              AND TrangThai = 'HOAT_DONG'
        )
            THROW 50003,
                N'Khách hàng không tồn tại hoặc không hoạt động.', 1;

        -- 6. Khóa phòng để bảo vệ kiểm tra lịch
        DECLARE @TrangThaiPhong VARCHAR(20);
        DECLARE @NgungSuDung BIT;

        SELECT
            @TrangThaiPhong = TrangThai,
            @NgungSuDung = NgungSuDung
        FROM dbo.Phong WITH (UPDLOCK, HOLDLOCK)
        WHERE MaPhong = @MaP;

        IF @TrangThaiPhong IS NULL
            THROW 50004, N'Phòng không tồn tại.', 1;

        IF @NgungSuDung = 1
           OR @TrangThaiPhong <> 'TRONG'
            THROW 50005,
                N'Phòng không ở trạng thái cho phép đặt mới.', 1;

        -- 7. Kiểm tra lịch giao nhau
        IF EXISTS (
            SELECT 1
            FROM dbo.DatPhong
            WHERE MaPhong = @MaP
              AND TrangThai IN ('GIU_PHONG', 'DANG_O')
              AND @NgayNhanDuKien < NgayTraDuKien
              AND @NgayTraDuKien > NgayNhanDuKien
        )
            THROW 50006,
                N'Phòng không còn khả dụng trong khoảng thời gian đã chọn. Hãy kiểm tra lại lịch.', 1;

        -- 8. Tạo đơn
        INSERT INTO dbo.DatPhong
        (
            MaDatPhong,
            MaKhachHang,
            MaPhong,
            GiaPhongChot,
            NgayNhanDuKien,
            NgayTraDuKien,
            TrangThai,
            MaLuotLuuTru
        )
        VALUES
        (
            @MaDP,
            @MaKH,
            @MaP,
            @Gia,
            @NgayNhanDuKien,
            @NgayTraDuKien,
            'GIU_PHONG',
            @MaLT
        );

        COMMIT TRANSACTION;

        SELECT
            N'TAO_DAT_PHONG_THANH_CONG' AS KetQua,
            @MaDP AS MaDatPhong,
            @MaP AS MaPhong,
            N'GIU_PHONG' AS TrangThai;

    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH;
END;
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE   PROCEDURE dbo.sp_DatPhong_TraPhong
    @MaDatPhong VARCHAR(MAX),
    @ThoiDiemTra DATETIME2(0) = NULL
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    BEGIN TRY
        IF @MaDatPhong IS NULL
           OR DATALENGTH(TRIM(@MaDatPhong)) NOT BETWEEN 1 AND 20
            THROW 50405, N'Mã đặt phòng không hợp lệ.', 1;

        DECLARE @MaDP VARCHAR(20) = TRIM(@MaDatPhong);

        BEGIN TRANSACTION;

        DECLARE
            @TrangThai VARCHAR(20),
            @NgayNhan DATETIME2(0),
            @ThoiDiemThucTe DATETIME2(0);

        SELECT
            @TrangThai = TrangThai,
            @NgayNhan = NgayNhan
        FROM dbo.DatPhong WITH (UPDLOCK, HOLDLOCK)
        WHERE MaDatPhong = @MaDP;

        IF @TrangThai IS NULL
            THROW 50401, N'Đơn đặt phòng không tồn tại.', 1;

        IF @TrangThai <> 'DANG_O'
            THROW 50402,
                N'Chỉ được trả phòng với đơn đang lưu trú.', 1;

        IF @NgayNhan IS NULL
            THROW 50403,
                N'Đơn chưa có thời điểm nhận phòng thực tế.', 1;

        SET @ThoiDiemThucTe =
            CONVERT(DATETIME2(0), SYSDATETIME());

        IF @ThoiDiemThucTe <= @NgayNhan
            THROW 50404,
                N'Thời điểm trả phải sau thời điểm nhận.', 1;

        UPDATE dbo.DatPhong
        SET
            TrangThai = 'DA_TRA',
            NgayTra = @ThoiDiemThucTe
        WHERE MaDatPhong = @MaDP
          AND TrangThai = 'DANG_O';

        IF @@ROWCOUNT <> 1
            THROW 50406, N'Không thể trả phòng.', 1;

        COMMIT TRANSACTION;

        SELECT
            N'TRA_PHONG_THANH_CONG' AS KetQua,
            @MaDP AS MaDatPhong,
            @ThoiDiemThucTe AS NgayTra,
            N'DA_TRA' AS TrangThai;

    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH;
END;
GO
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE   PROCEDURE dbo.sp_DatPhong_Xoa
    @MaDatPhong VARCHAR(MAX)
AS
BEGIN
    SET NOCOUNT ON;
    SET XACT_ABORT ON;

    BEGIN TRY
        IF @MaDatPhong IS NULL
           OR DATALENGTH(TRIM(@MaDatPhong)) NOT BETWEEN 1 AND 20
            THROW 50705, N'Mã đặt phòng không hợp lệ.', 1;

        DECLARE @MaDP VARCHAR(20) = TRIM(@MaDatPhong);
        DECLARE @TrangThai VARCHAR(20);

        BEGIN TRANSACTION;

        -- 1. Khóa đơn cần xóa
        SELECT @TrangThai = TrangThai
        FROM dbo.DatPhong WITH (UPDLOCK, HOLDLOCK)
        WHERE MaDatPhong = @MaDP;

        IF @TrangThai IS NULL
            THROW 50701, N'Đơn đặt phòng không tồn tại.', 1;

        -- 2. Chỉ xóa đơn đã hủy
        IF @TrangThai <> 'DA_HUY'
            THROW 50702, N'Chỉ được xóa đơn đã hủy.', 1;

        -- 3. Không xóa nếu còn dữ liệu liên quan
        IF EXISTS (
            SELECT 1 FROM dbo.ChuyenPhong
            WHERE MaDatPhongCu = @MaDP
               OR MaDatPhongMoi = @MaDP
        )
        OR EXISTS (
            SELECT 1 FROM dbo.PhongDanhGiaNhan
            WHERE MaDatPhong = @MaDP
        )
        OR EXISTS (
            SELECT 1 FROM dbo.PhongLuuTruNhan
            WHERE MaDatPhong = @MaDP
        )
        OR EXISTS (
            SELECT 1 FROM dbo.PhongPhanAnhNhan
            WHERE MaDatPhong = @MaDP
        )
        OR EXISTS (
            SELECT 1 FROM dbo.SuDungDichVu
            WHERE MaDatPhong = @MaDP
        )
        OR EXISTS (
            SELECT 1 FROM dbo.YeuCauDatPhong
            WHERE MaDatPhong = @MaDP
        )
            THROW 50703,
                N'Đơn có dữ liệu liên quan, không thể xóa.', 1;

        -- 4. Xóa đơn, KHÔNG xóa mã trong sổ đăng ký
        DELETE FROM dbo.DatPhong
        WHERE MaDatPhong = @MaDP
          AND TrangThai = 'DA_HUY';

        IF @@ROWCOUNT <> 1
            THROW 50704, N'Không thể xóa đơn đặt phòng.', 1;

        COMMIT TRANSACTION;

        SELECT
            N'XOA_DAT_PHONG_THANH_CONG' AS KetQua,
            @MaDP AS MaDatPhong;

    END TRY
    BEGIN CATCH
        IF XACT_STATE() <> 0
            ROLLBACK TRANSACTION;

        THROW;
    END CATCH;
END;
GO

-- Khôi phục trạng thái phiên nếu phần kiểm tra đầu file đã bật NOEXEC.
SET NOEXEC OFF;
GO
