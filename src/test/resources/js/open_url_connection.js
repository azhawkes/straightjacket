function open_url_connection(u) {
    var url = new java.net.URL(u);
    var connection = url.openConnection();
    var content = connection.getContent();

    return content;
}
