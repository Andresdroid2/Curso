package com.example.curso

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.font.PdfFont
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfPage
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class Generarcertificado : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_generarcertificado)

        val btnCertificado = findViewById<Button>(R.id.btnGenerarCertificado)
        btnCertificado.setOnClickListener {
            val usuario = RepositorioUsuarios.usuarioActual

            if (usuario == null) {
                Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Generar código de seguridad
            val codigoSeguridad = UUID.randomUUID().toString().substring(0, 8).uppercase()

            // Guardar en memoria
            usuario.codigoCertificado = codigoSeguridad

            // Generar PDF con el código incluido
            val archivo = generarCertificado(usuario.nombre, codigoSeguridad)

            // Enviar por correo
            enviarPorCorreo(archivo, usuario.email)

            Toast.makeText(
                this,
                "Certificado generado con código: $codigoSeguridad",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun generarCertificado(nombre: String, codigo: String): File {
        val archivo = File(getExternalFilesDir(null), "certificado_${nombre.replace(" ", "_")}.pdf")
        val pdfWriter = PdfWriter(archivo)
        val pdfDocument = PdfDocument(pdfWriter)
        val landscape: PageSize = PageSize.LETTER.rotate()
        val page: PdfPage? = pdfDocument.addNewPage(landscape)
        val canvas = PdfCanvas(page)
        val fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA)
        val fontNegrita = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)
        val fecha = SimpleDateFormat("dd 'de' MMMM 'de' yyyy", Locale("es")).format(Date())

        //colores
        val azul = DeviceRgb(23, 98, 255)

        // Dibuja un rectángulo como borde
        canvas.setLineWidth(12f)
        canvas.setStrokeColor(azul)
        canvas.rectangle(
            30.0, 30.0,
            (landscape.width - 60).toDouble(),
            (landscape.height - 60).toDouble()
        )
        canvas.stroke()


        // Dibuja un rectángulo como borde
        canvas.setLineWidth(2f)
        canvas.setStrokeColor(azul)
        canvas.rectangle(
            40.0, 40.0,
            (landscape.width - 80).toDouble(),
            (landscape.height - 80).toDouble()
        )
        canvas.stroke()


        // Tamaño de fuente
        val fontSize = 14f
        val fontSizeNombre = 16f
        val pageWidth = landscape.width
        var y = 510f
        val lineHeight = 22f

        // Función para centrar texto
        fun drawCenteredText(text: String, y: Float, font: PdfFont, size: Float) {
            val textWidth = font.getWidth(text, size)
            val x = (landscape.width - textWidth) / 2f
            canvas.beginText()
                .setFontAndSize(font, size)
                .moveText(x.toDouble(), y.toDouble())
                .showText(text)
                .endText()
        }

        //Títulos

        drawCenteredText("Crusos.com", y, fontRegular, fontSize)
        y -= lineHeight
        drawCenteredText(
            "Administrador de cursos.com",
            y,
            fontRegular,
            fontSize
        )
        y -= lineHeight
        drawCenteredText("CERTIFICA QUE:", y, fontRegular, fontSize)

        y -= 30f
        drawCenteredText(nombre, y, fontNegrita, fontSizeNombre)

        //Texto descriptivo del curso
        y -= lineHeight
        drawCenteredText("Has completado satisfactoriamente el curso:", y, fontRegular, fontSize)
        y -= lineHeight
        drawCenteredText(
            "“Gracías a tu esfuerzo esto fue posible, sigamos nos formando por un mejor futuro”",
            y,
            fontRegular,
            fontSize
        )

        val linea1 =
            "Curso dirigido a ****-****-****, con el propósito de fortalecer el vínculo *****-**** y fomentar"
        val linea2 = "una cultura de participación, identidad y responsabilidad."

        y -= lineHeight
        drawCenteredText(linea1, y, fontRegular, fontSize)
        y -= lineHeight
        drawCenteredText(linea2, y, fontRegular, fontSize)

        // Centrar título de la sección
        y -= 26f
        drawCenteredText("Contenido del curso:", y, fontRegular, fontSize)

        // Listado alineado a la izquierda
        val contenido = listOf(
            "• Bienvenida e introducción",
            "• ******",
            "• ******",
            "• *******",
            "• *******",
            "• *******"
        )

        val margenIzquierdo = 60f
        val fontContenido = 12f

        for (linea in contenido) {
            y -= 18f
            canvas.beginText()
                .setFontAndSize(fontRegular, fontContenido)
                .moveText(margenIzquierdo.toDouble(), y.toDouble())
                .showText(linea)
                .endText()
        }

        //Fecha centrada
        y -= 30f
        val textoFinal = "Finalizado el $fecha con una carga horaria de 10 horas."
        drawCenteredText(textoFinal, y, fontRegular, fontSize)
        y -= lineHeight
        drawCenteredText("\"Código: $codigo\"", y, fontRegular, fontSize)

        //Firmas
        val yFirmas = 90f
        val xIzquierda = 80f
        val xDerecha = pageWidth - 220f

        // Firma
        canvas.beginText()
            .setFontAndSize(fontRegular, 12f)
            .moveText(xIzquierda.toDouble(), yFirmas.toDouble())
            .showText("******-****-****")
            .moveText(0.0, -14.0)
            .showText("Rector ****")
            .endText()





        // Firma
        canvas.beginText()
            .setFontAndSize(fontRegular, 12f)
            .moveText(xDerecha.toDouble(), yFirmas.toDouble())
            .showText("***-****-****")
            .moveText(0.0, -14.0)
            .showText("***-****")
            .endText()

        pdfDocument.close()
        return archivo
    }

    private fun enviarPorCorreo(archivo: File, correoDestino: String) {
        val uri: Uri = FileProvider.getUriForFile(this, "$packageName.provider", archivo)
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_SUBJECT, "Certificado del Curso ****")
            putExtra(
                Intent.EXTRA_TEXT,
                "Adjunto encontrará su certificado del curso 'Conociendo la ****'."
            )
            putExtra(Intent.EXTRA_STREAM, uri)
            if (correoDestino.isNotEmpty()) putExtra(Intent.EXTRA_EMAIL, arrayOf(correoDestino))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Enviar certificado por correo..."))
    }
}