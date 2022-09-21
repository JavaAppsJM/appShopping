package be.hvwebsites.shopping.services;

public class FileBaseService {
    private String deviceModel;
    private String fileBase;
    private static final String FILE_BASE_INTERNAL = "base_internal";
    private static final String FILE_BASE_EXTERNAL = "base_external";

    public FileBaseService(String deviceModel) {
        this.deviceModel = deviceModel;
        if (deviceModel.equals("GT-I9100")){
            fileBase = FILE_BASE_INTERNAL;
        } else {
            fileBase = FILE_BASE_EXTERNAL;
        }
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    public String getFileBase() {
        return fileBase;
    }

    public void setFileBase(String fileBase) {
        this.fileBase = fileBase;
    }
}
