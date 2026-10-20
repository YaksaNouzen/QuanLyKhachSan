package service;

import java.sql.Timestamp;
import dao.DatPhongDAO;
import model.DatPhong;
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

public void nhanPhong(String maDatPhong) throws SQLException {
    datPhongDAO.nhanPhong(maDatPhong);
}

public void traPhong(String maDatPhong) throws SQLException {
    datPhongDAO.traPhong(maDatPhong);
}

public List<String> timPhongTrong(
        Timestamp ngayNhanDuKien,
        Timestamp ngayTraDuKien) throws SQLException {

    return datPhongDAO.timPhongTrong(
            ngayNhanDuKien,
            ngayTraDuKien
    );
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