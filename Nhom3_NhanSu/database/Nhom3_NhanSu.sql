-- =============================================
-- NHÓM 3: NHÂN SỰ & HỆ THỐNG
-- Bảng: VaiTro, NhanVien, TaiKhoan, CaLam, PhanCa
-- Chạy trên SQL Server, trong database chung của cả nhóm
-- =============================================
-- Tạo database nếu chưa có (nếu nhóm đã có database chung thì bỏ qua)
IF DB_ID('QuanLyKhachSan') IS NULL
    CREATE DATABASE QuanLyKhachSan;
GO
USE QuanLyKhachSan;
GO

-- 1. Bảng VAI TRÒ (Admin, Lễ tân, Kế toán, Nhân viên)
CREATE TABLE VaiTro (
    MaVaiTro   VARCHAR(10)   PRIMARY KEY,
    TenVaiTro  NVARCHAR(50)  NOT NULL
);

-- 2. Bảng NHÂN VIÊN
CREATE TABLE NhanVien (
    MaNV        VARCHAR(10)   PRIMARY KEY,
    HoTen       NVARCHAR(100) NOT NULL,
    GioiTinh    NVARCHAR(5),
    NgaySinh    DATE,
    SDT         VARCHAR(15),
    Email       VARCHAR(100),
    DiaChi      NVARCHAR(200),
    BoPhan      NVARCHAR(50),             -- Lễ tân, Buồng phòng, Kế toán, Bảo vệ, Quản lý
    ChucVu      NVARCHAR(50),             -- Trưởng bộ phận, Nhân viên, Thực tập
    NgayVaoLam  DATE,
    TrangThai   NVARCHAR(30) DEFAULT N'Đang làm'
        CHECK (TrangThai IN (N'Đang làm', N'Nghỉ phép', N'Đã nghỉ việc'))
);

-- 3. Bảng TÀI KHOẢN (mỗi nhân viên có tối đa 1 tài khoản)
CREATE TABLE TaiKhoan (
    TenDangNhap  VARCHAR(50)  PRIMARY KEY,
    MatKhau      VARCHAR(64)  NOT NULL,   -- lưu mã băm SHA-256, không lưu mật khẩu thật
    MaNV         VARCHAR(10)  NOT NULL UNIQUE,
    MaVaiTro     VARCHAR(10)  NOT NULL,
    TrangThai    BIT DEFAULT 1,           -- 1: hoạt động, 0: bị khóa
    FOREIGN KEY (MaNV)     REFERENCES NhanVien(MaNV),
    FOREIGN KEY (MaVaiTro) REFERENCES VaiTro(MaVaiTro)
);

-- 4. Bảng CA LÀM
CREATE TABLE CaLam (
    MaCa        VARCHAR(10)  PRIMARY KEY,
    TenCa       NVARCHAR(50) NOT NULL,
    GioBatDau   TIME NOT NULL,
    GioKetThuc  TIME NOT NULL             -- nếu nhỏ hơn giờ bắt đầu => ca qua đêm
);

-- 5. Bảng PHÂN CA (nhân viên nào làm ca nào, ngày nào) + thông tin bàn giao
CREATE TABLE PhanCa (
    MaPhanCa        INT IDENTITY(1,1) PRIMARY KEY,
    MaNV            VARCHAR(10) NOT NULL,
    MaCa            VARCHAR(10) NOT NULL,
    NgayLam         DATE NOT NULL,
    TrangThai       NVARCHAR(30) DEFAULT N'Chưa bàn giao',  -- Chưa bàn giao / Đã bàn giao
    NoiDungBanGiao  NVARCHAR(500),
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV),
    FOREIGN KEY (MaCa) REFERENCES CaLam(MaCa),
    -- 1 nhân viên không được phân cùng 1 ca 2 lần trong 1 ngày
    UNIQUE (MaNV, MaCa, NgayLam)
);
GO

-- ============ DỮ LIỆU MẪU ============
INSERT INTO VaiTro VALUES
('ADMIN',  N'Admin'),
('LETAN',  N'Lễ tân'),
('KETOAN', N'Kế toán'),
('NV',     N'Nhân viên');

INSERT INTO NhanVien VALUES
('NV001', N'Trần Đức Việt',     N'Nam', '2004-08-15', '0901234567', 'viet@gmail.com',   N'Hà Nội',    N'Quản lý',     N'Trưởng bộ phận', '2023-01-10', N'Đang làm'),
('NV002', N'Duy Nguyễn',        N'Nam', '2004-03-22', '0912345678', 'duy@gmail.com',    N'Hà Nội',    N'Lễ tân',      N'Nhân viên',      '2023-05-02', N'Đang làm'),
('NV003', N'Doãn Công Trường',  N'Nam', '2004-11-03', '0923456789', 'truong@gmail.com', N'Hải Phòng', N'Kế toán',     N'Nhân viên',      '2023-08-15', N'Đang làm'),
('NV004', N'Nguyễn Tuấn Đạt',   N'Nam', '2004-02-14', '0934567890', 'dat@gmail.com',    N'Nam Định',  N'Buồng phòng', N'Nhân viên',      '2024-03-01', N'Nghỉ phép'),
('NV005', N'Nguyễn Thế Anh',    N'Nam', '2004-06-30', '0945678901', 'theanh@gmail.com', N'Hà Nội',    N'Bảo vệ',      N'Nhân viên',      '2024-06-01', N'Đang làm');

-- NV001 Trần Đức Việt là Quản lý -> tài khoản admin
-- NV005 Nguyễn Thế Anh chưa có tài khoản (để demo chức năng tạo tài khoản)
-- Mật khẩu mẫu của tất cả tài khoản là: 123456
-- (SQL Server tự băm SHA-256 giống hàm MaHoa.sha256 trong Java)
INSERT INTO TaiKhoan VALUES
('admin',  CONVERT(VARCHAR(64), HASHBYTES('SHA2_256', '123456'), 2), 'NV001', 'ADMIN',  1),
('letan',  CONVERT(VARCHAR(64), HASHBYTES('SHA2_256', '123456'), 2), 'NV002', 'LETAN',  1),
('ketoan', CONVERT(VARCHAR(64), HASHBYTES('SHA2_256', '123456'), 2), 'NV003', 'KETOAN', 1),
('nhanvien',CONVERT(VARCHAR(64), HASHBYTES('SHA2_256', '123456'), 2), 'NV004', 'NV',     1);

INSERT INTO CaLam VALUES
('CA1', N'Ca sáng', '06:00', '14:00'),
('CA2', N'Ca chiều','14:00', '22:00'),
('CA3', N'Ca đêm',  '22:00', '06:00'),
('CA4', N'Ca hành chính', '08:00', '17:00');

INSERT INTO PhanCa (MaNV, MaCa, NgayLam) VALUES
('NV002', 'CA1', '2026-10-25'),
('NV004', 'CA2', '2026-10-25'),
('NV003', 'CA4', '2026-10-26');
GO
