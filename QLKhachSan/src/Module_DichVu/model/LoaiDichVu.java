
package Module_DichVu.model;

public class LoaiDichVu {

    private int maLoaiDV;
    private String tenLoaiDV;

    public LoaiDichVu() {
    }

    public LoaiDichVu(int maLoaiDV, String tenLoaiDV) {
        this.maLoaiDV = maLoaiDV;
        this.tenLoaiDV = tenLoaiDV;
    }

    public int getMaLoaiDV() {
        return maLoaiDV;
    }

    public void setMaLoaiDV(int maLoaiDV) {
        this.maLoaiDV = maLoaiDV;
    }

    public String getTenLoaiDV() {
        return tenLoaiDV;
    }

    public void setTenLoaiDV(String tenLoaiDV) {
        this.tenLoaiDV = tenLoaiDV;
    }

    // Hien thi ten loai trong JComboBox
    @Override
    public String toString() {
        return tenLoaiDV;
    }
}
