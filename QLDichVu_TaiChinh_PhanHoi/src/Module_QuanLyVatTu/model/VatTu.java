package Module_QuanLyVatTu.model;

public class VatTu {
    private String maVatTu;
    private String tenVatTu;
    private String donViTinh;
    private int soLuongTon;
    private int mucTonToiThieu;
    private String trangThai;
    private String moTa;

    public VatTu() {
    }

    public VatTu(String maVatTu, String tenVatTu, String donViTinh,
                 int soLuongTon, int mucTonToiThieu,
                 String trangThai, String moTa) {
        this.maVatTu = maVatTu;
        this.tenVatTu = tenVatTu;
        this.donViTinh = donViTinh;
        this.soLuongTon = soLuongTon;
        this.mucTonToiThieu = mucTonToiThieu;
        this.trangThai = trangThai;
        this.moTa = moTa;
    }

    public String getMaVatTu() { return maVatTu; }
    public void setMaVatTu(String maVatTu) { this.maVatTu = maVatTu; }
    public String getTenVatTu() { return tenVatTu; }
    public void setTenVatTu(String tenVatTu) { this.tenVatTu = tenVatTu; }
    public String getDonViTinh() { return donViTinh; }
    public void setDonViTinh(String donViTinh) { this.donViTinh = donViTinh; }
    public int getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(int soLuongTon) { this.soLuongTon = soLuongTon; }
    public int getMucTonToiThieu() { return mucTonToiThieu; }
    public void setMucTonToiThieu(int mucTonToiThieu) { this.mucTonToiThieu = mucTonToiThieu; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }

    public boolean sapHetHang() {
        return soLuongTon <= mucTonToiThieu;
    }

    @Override
    public String toString() {
        return maVatTu + " - " + tenVatTu;
    }
}
