# SQL patches cua Module Phong Nhom 2

Patch chay tren database SQL Server da tao theo `QuanLyDatPhong/database/schema.sql`.
Khong chay tren database dung chung khi chua sao luu va chua duoc truong nhom xac nhan.

1. Chon dung database thu nghiem trong SQL Server Management Studio.
2. Chay `N2_001_buong_phong_va_bao_tri.sql`. Patch them cot, backfill trang thai
   phieu cu, cho phep BatDau NULL truoc khi cong viec that su bat dau. Script co
   transaction va co the chay lai.
3. Chay `N2_002_grant_quyen_nhom2.sql` bang tai khoan duoc phep quan tri.
   Patch chi cap INSERT/UPDATE/DELETE can thiet; khong cap quyen quan tri hay
   quyen xoa phong, phieu ve sinh, phieu bao tri.
4. Doi chieu cot bang bang `SchemaCheckNhom2` hoac cac SELECT TOP 0 o cuoi patch.

`hotelbooking_app` can co quyen doc theo cau hinh chung cua Nhom 1. Patch 002
khong thay doi quyen cua KhachHang hay DatPhong.
