-- =============================================
-- DỮ LIỆU CHẠY THỬ (chỉ dùng khi không có SQL Server)
-- Chạy trên H2 (database trong bộ nhớ), tắt chương trình là mất.
-- Cấu trúc bảng giống hệt database/Nhom3_NhanSu.sql
-- =============================================

CREATE TABLE VaiTro (
    MaVaiTro   VARCHAR(10)   PRIMARY KEY,
    TenVaiTro  NVARCHAR(50)  NOT NULL
);

CREATE TABLE NhanVien (
    MaNV        VARCHAR(10)   PRIMARY KEY,
    HoTen       NVARCHAR(100) NOT NULL,
    GioiTinh    NVARCHAR(5),
    NgaySinh    DATE,
    SDT         VARCHAR(15),
    Email       VARCHAR(100),
    DiaChi      NVARCHAR(200),
    BoPhan      NVARCHAR(50),
    ChucVu      NVARCHAR(50),
    NgayVaoLam  DATE,
    TrangThai   NVARCHAR(30) DEFAULT N'Đang làm'
        CHECK (TrangThai IN (N'Đang làm', N'Nghỉ phép', N'Đã nghỉ việc'))
);

CREATE TABLE TaiKhoan (
    TenDangNhap  VARCHAR(50)  PRIMARY KEY,
    MatKhau      VARCHAR(64)  NOT NULL,
    MaNV         VARCHAR(10)  NOT NULL UNIQUE,
    MaVaiTro     VARCHAR(10)  NOT NULL,
    TrangThai    BIT DEFAULT 1,
    FOREIGN KEY (MaNV)     REFERENCES NhanVien(MaNV),
    FOREIGN KEY (MaVaiTro) REFERENCES VaiTro(MaVaiTro)
);

CREATE TABLE CaLam (
    MaCa        VARCHAR(10)  PRIMARY KEY,
    TenCa       NVARCHAR(50) NOT NULL,
    GioBatDau   TIME NOT NULL,
    GioKetThuc  TIME NOT NULL
);

CREATE TABLE PhanCa (
    MaPhanCa        INT IDENTITY(1,1) PRIMARY KEY,
    MaNV            VARCHAR(10) NOT NULL,
    MaCa            VARCHAR(10) NOT NULL,
    NgayLam         DATE NOT NULL,
    TrangThai       NVARCHAR(30) DEFAULT N'Chưa bàn giao',
    NoiDungBanGiao  NVARCHAR(500),
    FOREIGN KEY (MaNV) REFERENCES NhanVien(MaNV),
    FOREIGN KEY (MaCa) REFERENCES CaLam(MaCa),
    UNIQUE (MaNV, MaCa, NgayLam)
);

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
-- Mật khẩu của tất cả tài khoản: 123456 (đã băm SHA-256)
INSERT INTO TaiKhoan VALUES
('admin',    '8D969EEF6ECAD3C29A3A629280E686CF0C3F5D5A86AFF3CA12020C923ADC6C92', 'NV001', 'ADMIN',  1),
('letan',    '8D969EEF6ECAD3C29A3A629280E686CF0C3F5D5A86AFF3CA12020C923ADC6C92', 'NV002', 'LETAN',  1),
('ketoan',   '8D969EEF6ECAD3C29A3A629280E686CF0C3F5D5A86AFF3CA12020C923ADC6C92', 'NV003', 'KETOAN', 1),
('nhanvien', '8D969EEF6ECAD3C29A3A629280E686CF0C3F5D5A86AFF3CA12020C923ADC6C92', 'NV004', 'NV',     1);

INSERT INTO CaLam VALUES
('CA1', N'Ca sáng',       '06:00', '14:00'),
('CA2', N'Ca chiều',      '14:00', '22:00'),
('CA3', N'Ca đêm',        '22:00', '06:00'),
('CA4', N'Ca hành chính', '08:00', '17:00');

-- Lịch làm tính theo ngày chạy chương trình, để mở ra là có lịch hôm nay
INSERT INTO PhanCa (MaNV, MaCa, NgayLam) VALUES
('NV002', 'CA1', CURRENT_DATE),
('NV004', 'CA2', CURRENT_DATE),
('NV003', 'CA4', DATEADD(DAY, 1, CURRENT_DATE));
