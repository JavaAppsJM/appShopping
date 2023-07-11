package be.hvwebsites.shopping.services;

public class FileBaseService {
    private String deviceModel;
    private String packName;
    private String fileBase;
    private String fileBaseDir;
    private String fileBaseDirInt;
    private String fileBaseDirExt;
    public static final String FILE_BASE_INTERNAL = "base_internal";
    public static final String FILE_BASE_EXTERNAL = "base_external";
    public static final String SAMSUNG_GTI9100 = "GT-I9100";
    // "/storage/emulated/0/Android/data/be.hvwebsites.shopping/files"
    // "/data/user/0/be.hvwebsites.shopping/files"

    public FileBaseService(String deviceModel, String packageNm) {
        this.deviceModel = deviceModel;
        this.packName = packageNm;
        this.fileBaseDirInt = "/data/user/0/" + packageNm + "/files";
        this.fileBaseDirExt = "/storage/emulated/0/Android/data/" + packageNm + "/files";

        if (deviceModel.equals(SAMSUNG_GTI9100)){
            fileBase = FILE_BASE_INTERNAL;
            this.fileBaseDir = "/data/user/0/" + packageNm + "/files";
        } else {
            fileBase = FILE_BASE_EXTERNAL;
            this.fileBaseDir = "/storage/emulated/0/Android/data/" + packageNm + "/files";
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
        if (fileBase.equals(FILE_BASE_INTERNAL)){
            this.fileBaseDir = "/data/user/0/" + packName + "/files";
        }else {
            this.fileBaseDir = "/storage/emulated/0/Android/data/" + packName + "/files";
        }
    }

    public String getFileBaseDir() {
        return fileBaseDir;
    }
}
