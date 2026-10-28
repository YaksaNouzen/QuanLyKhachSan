package Module_NhaCungCap.model;

public class NhaCungCap {
    private String maNhaCungCap;
    private String tenNhaCungCap;
    private String soDienThoai;
    private String email;
    private String diaChi;
    private String trangThai;
    private String ghiChu;

    public NhaCungCap() {
    }

    public NhaCungCap(String maNhaCungCap, String tenNhaCungCap,
                      String soDienThoai, String email, String diaChi,
                      String trangThai, String ghiChu) {
        this.maNhaCungCap = maNhaCungCap;
        this.tenNhaCungCap = tenNhaCungCap;
        this.soDienThoai = soDienThoai;
        this.email = email;
        this.diaChi = diaChi;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
    }

    public String getMaNhaCungCap() { return maNhaCungCap; }
    public void setMaNhaCungCap(String value) { this.maNhaCungCap = value; }
    public String getTenNhaCungCap() { return tenNhaCungCap; }
    public void setTenNhaCungCap(String value) { this.tenNhaCungCap = value; }
    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String value) { this.soDienThoai = value; }
    public String getEmail() { return email; }
    public void setEmail(String value) { this.email = value; }
    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String value) { this.diaChi = value; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String value) { this.trangThai = value; }
    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String value) { this.ghiChu = value; }

    @Override
    public String toString() {
        return maNhaCungCap + " - " + tenNhaCungCap;
    }
}
