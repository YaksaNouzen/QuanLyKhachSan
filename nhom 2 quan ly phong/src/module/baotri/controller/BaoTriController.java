package module.baotri.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import module.baotri.model.MucDoUuTien;
import module.baotri.model.PhieuBaoTri;
import module.baotri.service.BaoTriService;
import java.math.BigDecimal;

public class BaoTriController {
    private final BaoTriService service;
    private List<PhieuBaoTri> data = List.of();
    private final List<Runnable> listeners = new ArrayList<>();
    private Consumer<String> errors = message -> {};
    private Consumer<Boolean> busyListener = value -> {};
    private boolean busy;

    public BaoTriController(BaoTriService service) {
        this.service = service;
    }

    public List<PhieuBaoTri> phieus() {
        return data;
    }

    public void onChange(Runnable listener) {
        listeners.add(listener);
    }

    public void onError(Consumer<String> listener) {
        errors = listener;
    }

    public void onBusy(Consumer<Boolean> listener) {
        busyListener = listener;
    }

    public void refresh() {
        run(() -> {}, () -> {});
    }

    public void baoHong(String maPhong, String noiDung, String nguoiBao, MucDoUuTien mucDo) {
        run(() -> service.baoHong(maPhong, noiDung, nguoiBao, mucDo), () -> {});
    }

    public void batDauSua(String maBaoTri, String nguoiPhuTrach) {
        run(() -> service.lapPhieuVaBatDauSua(maBaoTri, nguoiPhuTrach), () -> {});
    }

    public void hoanTat(String maBaoTri, BigDecimal chiPhi, String ketQua) {
        run(() -> service.hoanTatSua(maBaoTri, chiPhi, ketQua), () -> {});
    }

    public void huy(String maBaoTri) {
        run(() -> service.huyBaoHong(maBaoTri), () -> {});
    }

    private void run(Runnable action, Runnable success) {
        if (!SwingUtilities.isEventDispatchThread())
            throw new IllegalStateException("Controller phai duoc goi tren EDT.");
        if (busy) {
            errors.accept("Dang xu ly, vui long cho.");
            return;
        }
        busy = true;
        busyListener.accept(true);
        new SwingWorker<List<PhieuBaoTri>, Void>() {
            protected List<PhieuBaoTri> doInBackground() {
                action.run();
                return service.danhSach();
            }

            protected void done() {
                try {
                    data = get();
                    listeners.forEach(Runnable::run);
                    success.run();
                } catch (Exception ex) {
                    Throwable cause = ex.getCause() == null ? ex : ex.getCause();
                    errors.accept(cause.getMessage());
                } finally {
                    busy = false;
                    busyListener.accept(false);
                }
            }
        }.execute();
    }
}
