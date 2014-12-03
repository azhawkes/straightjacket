function touch_a_file(path) {
    var file = new java.io.File(path);

    return file.createNewFile();
}
