package Module_PhanHoiKhachHang.model;

public class PhanHoi {
    private long maPhanHoi;
    public long getMaPhanHoi() { return maPhanHoi; }
    public void setMaPhanHoi(long value) { this.maPhanHoi = value; }
    private String maKhachHang;
    public String getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(String value) { this.maKhachHang = value; }
    private LoaiPhanHoi loaiPhanHoi;
    public LoaiPhanHoi getLoaiPhanHoi() { return loaiPhanHoi; }
    public void setLoaiPhanHoi(LoaiPhanHoi value) { this.loaiPhanHoi = value; }
    private String noiDung;
    public String getNoiDung() { return noiDung; }
    public void setNoiDung(String value) { this.noiDung = value; }
    private java.time.LocalDateTime thoiGianTiepNhan;
    public java.time.LocalDateTime getThoiGianTiepNhan() { return thoiGianTiepNhan; }
    public void setThoiGianTiepNhan(java.time.LocalDateTime value) { this.thoiGianTiepNhan = value; }
    private TrangThaiPhanHoi trangThai;
    public TrangThaiPhanHoi getTrangThai() { return trangThai; }
    public void setTrangThai(TrangThaiPhanHoi value) { this.trangThai = value; }
    private java.time.LocalDateTime thoiGianCapNhat;
    public java.time.LocalDateTime getThoiGianCapNhat() { return thoiGianCapNhat; }
    public void setThoiGianCapNhat(java.time.LocalDateTime value) { this.thoiGianCapNhat = value; }
    private byte[] phienBan;
    public byte[] getPhienBan() { return phienBan; }
    public void setPhienBan(byte[] value) { this.phienBan = value; }
}
