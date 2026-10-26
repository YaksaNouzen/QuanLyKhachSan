package module.datphong.service;

import java.sql.Timestamp;
import module.datphong.dao.DatPhongDAO;
import module.datphong.model.DatPhong;
import java.sql.SQLException;
import java.util.List;

public class DatPhongService {

    private final DatPhongDAO datPhongDAO;

    public DatPhongService() {
        this.datPhongDAO = new DatPhongDAO();
    }

    public List<DatPhong> layDanhSachDatPhong() throws SQLException {
        return datPhongDAO.layDanhSachDatPhong();
    }
    public void taoDatPhong(DatPhong dp) throws SQLException {
    datPhongDAO.taoDatPhong(dp);
}
    
    
public void capNhatLichDatPhong(
        DatPhong dp,
        Timestamp ngayNhanGoc,
        Timestamp ngayTraGoc) throws SQLException {

    datPhongDAO.capNhatLichDatPhong(
            dp,
            ngayNhanGoc,
            ngayTraGoc
    );
}

public void huyDatPhong(String maDatPhong) throws SQLException {
    datPhongDAO.huyDatPhong(maDatPhong);
}

public void xoaDatPhong(String maDatPhong) throws SQLException {
    datPhongDAO.xoaDatPhong(maDatPhong);
}

public List<String> timPhongTrong(
        Timestamp ngayNhanDuKien,
        Timestamp ngayTraDuKien) throws SQLException {

    return datPhongDAO.timPhongTrong(
            ngayNhanDuKien,
            ngayTraDuKien
    );
}

public List<String> timPhongTrongDeDoi(String maDatPhongCu) throws SQLException {
    if (maDatPhongCu == null || maDatPhongCu.trim().isEmpty()
            || maDatPhongCu.trim().length() > 20) {
        throw new IllegalArgumentException("Mã đặt phòng cũ không hợp lệ.");
    }
    return datPhongDAO.timPhongTrongDeDoi(maDatPhongCu.trim());
}


public void doiPhong(
        String maDatPhongCu,
        String maPhongMoi,
        String lyDo,
        String nguoiThucHien,
        String maDatPhongMoi,
        String maChuyenPhong) throws SQLException {

    datPhongDAO.doiPhong(
            maDatPhongCu,
            maPhongMoi,
            lyDo,
            nguoiThucHien,
            maDatPhongMoi,
            maChuyenPhong
    );
}


}
