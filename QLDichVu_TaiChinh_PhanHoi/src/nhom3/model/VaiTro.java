package nhom3.model;

public class VaiTro {

    private String maVaiTro;
    private String tenVaiTro;

    public VaiTro() {
    }

    public VaiTro(String maVaiTro, String tenVaiTro) {
        this.maVaiTro = maVaiTro;
        this.tenVaiTro = tenVaiTro;
    }

    public String getMaVaiTro() {
        return maVaiTro;
    }

    public void setMaVaiTro(String maVaiTro) {
        this.maVaiTro = maVaiTro;
    }

    public String getTenVaiTro() {
        return tenVaiTro;
    }

    public void setTenVaiTro(String tenVaiTro) {
        this.tenVaiTro = tenVaiTro;
    }

    // Hiển thị trong JComboBox
    @Override
    public String toString() {
        return tenVaiTro;
    }
}
