package com.pt.filetransfer.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 韬 on 2017-05-25.
 */
public class FileGroup {

    public static  class AVFiles {
        public static List<FileInfo> musicFiles = new ArrayList<FileInfo>();
        public static  List<FileInfo> mediaFiles = new ArrayList<FileInfo>();
        public static void setMusicFiles(List<FileInfo> musicFiles) {
            FileGroup.AVFiles.musicFiles = musicFiles;
        }

        public static void setMediaFiles(List<FileInfo> mediaFiles) {
            AVFiles.mediaFiles = mediaFiles;
        }
    }

    public static  class DOCFiles {
        public static List<FileInfo> docFiles = new ArrayList<FileInfo>();
        public static List<FileInfo> pdfFiles = new ArrayList<FileInfo>();
        public static void setDocFiles(List<FileInfo> docFiles) {
            FileGroup.DOCFiles.docFiles = docFiles;
        }

        public static void setPdfFiles(List<FileInfo> pdfFiles) {
            FileGroup.DOCFiles.pdfFiles = pdfFiles;
        }
    }

    public static  class OtherFiles {
        public static List<FileInfo> txtFles = new ArrayList<FileInfo>();

        public static void setTxtFles(List<FileInfo> txtFles) {
            OtherFiles.txtFles = txtFles;
        }
    }
}
