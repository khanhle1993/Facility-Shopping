package com.example.sendmail

import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import javax.activation.DataSource

class ByteArrayDataSource : DataSource {
    private var data: ByteArray? = null
    private var type: String? = null

    constructor(data: ByteArray?, type: String?) {
        this.data = data
        this.type = type
    }


    override fun getName(): String {
        return "ByteArrayDataSource"
    }

    override fun getOutputStream(): OutputStream {
        throw IOException("Not Supported")
    }

    override fun getInputStream(): InputStream {
        return ByteArrayInputStream(data)
    }

    override fun getContentType(): String {
        if (type == null)
            return "application/octet-stream";
        else
            return type as String
    }

}