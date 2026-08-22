import { Injectable } from '@angular/core';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

import { Routine, RoutineDay, Exercise } from '../../models/Routine';

interface PdfTheme {
  primary: [number, number, number];
  secondary: [number, number, number];
  dark: [number, number, number];
  light: [number, number, number];
  gray: [number, number, number];
  gold: [number, number, number];
}

@Injectable({
  providedIn: 'root'
})
export class PdfService {

  private readonly theme: PdfTheme = {

    // Rojo
    primary: [210, 44, 44],

    // Gris claro
    secondary: [245, 245, 245],

    // Negro/gris oscuro
    dark: [30, 30, 30],

    // Blanco
    light: [255, 255, 255],

    // Gris
    gray: [110, 110, 110],

    // Dorado del gimnasio
    gold: [201, 168, 62]
  };

  /**
   * Genera y descarga el PDF completo de la rutina.
   */
  async generateRoutinePdf(routine: Routine): Promise<void> {

    const doc = new jsPDF({
      orientation: 'portrait',
      unit: 'mm',
      format: 'a4'
    });

    /*
     * Cargamos el logo antes de comenzar.
     */
    const logo = await this.loadImage(
      '/assets/img/logo.png'
    );

    /*
     * Header de la primera página.
     */
    this.addHeader(doc, logo);

    /*
     * Información general de la rutina.
     */
    let currentY = this.addRoutineInfo(doc, routine);

    /*
     * Días y ejercicios.
     */
    currentY = this.addRoutineDays(
      doc,
      routine,
      currentY
    );

    /*
     * Footer de TODAS las páginas.
     */
    this.addFooters(doc);

    /*
     * Nombre seguro del archivo.
     */
    const fileName = this.sanitizeFileName(
      routine.name || 'rutina'
    );

    doc.save(`${fileName}.pdf`);
  }


  // ============================================================
  // LOGO
  // ============================================================

  private loadImage(src: string): Promise<HTMLImageElement> {

    return new Promise((resolve, reject) => {

      const img = new Image();

      img.onload = () => resolve(img);

      img.onerror = () =>
        reject(
          new Error(`No se pudo cargar la imagen: ${src}`)
        );

      img.src = src;
    });
  }


  // ============================================================
  // HEADER
  // ============================================================

  private addHeader(
    doc: jsPDF,
    logo: HTMLImageElement
  ): void {

    /*
     * Fondo negro del encabezado.
     */
    doc.setFillColor(...this.theme.dark);

    doc.rect(
      0,
      0,
      210,
      28,
      'F'
    );

    /*
     * Línea dorada inferior.
     */
    doc.setFillColor(...this.theme.gold);

    doc.rect(
      0,
      28,
      210,
      1.5,
      'F'
    );

    /*
     * Logo.
     */
    doc.addImage(
      logo,
      'PNG',
      12,
      3,
      24,
      23
    );

    /*
     * Nombre del gimnasio.
     */
    doc.setFont(
      'helvetica',
      'bold'
    );

    doc.setFontSize(17);

    doc.setTextColor(
      ...this.theme.light
    );

    doc.text(
      'CENTRO DEPORTIVO',
      44,
      14
    );

    /*
     * Subtítulo.
     */
    doc.setFont(
      'helvetica',
      'normal'
    );

    doc.setFontSize(7.5);

    doc.setTextColor(
      ...this.theme.gold
    );

    doc.text(
      'DESDE 1978 • DISCIPLINA • FUERZA • DETERMINACIÓN',
      44,
      20
    );
  }


  // ============================================================
  // INFORMACIÓN DE LA RUTINA
  // ============================================================

  private addRoutineInfo(
    doc: jsPDF,
    routine: Routine
  ): number {

    let y = 43;

    /*
     * Título.
     */
    doc.setFont(
      'helvetica',
      'bold'
    );

    doc.setFontSize(24);

    doc.setTextColor(
      ...this.theme.primary
    );

    doc.text(
      routine.name,
      20,
      y
    );

    y += 9;

    /*
     * Línea dorada.
     */
    doc.setDrawColor(
      ...this.theme.gold
    );

    doc.setLineWidth(0.8);

    doc.line(
      20,
      y,
      190,
      y
    );

    y += 9;

    /*
     * Descripción.
     */
    if (routine.description) {

      doc.setFont(
        'helvetica',
        'normal'
      );

      doc.setFontSize(10.5);

      doc.setTextColor(
        ...this.theme.dark
      );

      const descriptionLines =
        doc.splitTextToSize(
          routine.description,
          170
        );

      doc.text(
        descriptionLines,
        20,
        y
      );

      y +=
        descriptionLines.length * 5 +
        5;
    }

    /*
     * Badges.
     */
    const badges = [

      `NIVEL: ${routine.level}`,

      `OBJETIVO: ${routine.goal}`,

      `${routine.durationWeeks} SEMANAS`,

      `${routine.daysPerWeek} DÍAS/SEMANA`
    ];

    let badgeX = 20;

    badges.forEach(
      (badge, index) => {

        const width =
          this.getBadgeWidth(
            doc,
            badge
          );

        /*
         * Si no entra en la línea,
         * comenzamos una nueva.
         */
        if (
          badgeX + width >
          190
        ) {

          badgeX = 20;

          y += 10;
        }

        /*
         * Fondo.
         */
        if (
          index < 2
        ) {

          doc.setFillColor(
            ...this.theme.primary
          );

        } else {

          doc.setFillColor(
            ...this.theme.dark
          );
        }

        doc.setDrawColor(
          ...this.theme.gold
        );

        doc.setLineWidth(0.6);

        doc.roundedRect(
          badgeX,
          y,
          width,
          9,
          1.5,
          1.5,
          'FD'
        );

        /*
         * Texto.
         */
        doc.setFont(
          'helvetica',
          'bold'
        );

        doc.setFontSize(7.8);

        doc.setTextColor(
          ...this.theme.light
        );

        doc.text(
          badge,
          badgeX + width / 2,
          y + 5.8,
          {
            align: 'center'
          }
        );

        badgeX += width + 5;
      }
    );

    y += 17;

    /*
     * Título de sección.
     */
    doc.setFont(
      'helvetica',
      'bold'
    );

    doc.setFontSize(17);

    doc.setTextColor(
      ...this.theme.gold
    );

    doc.text(
      'DÍAS DE ENTRENAMIENTO',
      20,
      y
    );

    y += 5;

    /*
     * Línea.
     */
    doc.setDrawColor(
      ...this.theme.gold
    );

    doc.setLineWidth(0.6);

    doc.line(
      20,
      y,
      190,
      y
    );

    return y + 9;
  }


  // ============================================================
  // DÍAS
  // ============================================================

  private addRoutineDays(
    doc: jsPDF,
    routine: Routine,
    startY: number
  ): number {

    let currentY = startY;

    routine.routineDays.forEach(
      (day, dayIndex) => {

        /*
         * Altura aproximada necesaria para
         * comenzar un nuevo día.
         */
        const minimumDayHeight = 30;

        if (
          currentY +
          minimumDayHeight >
          265
        ) {

          doc.addPage();

          currentY = 18;
        }

        currentY =
          this.addDayHeader(
            doc,
            day,
            currentY
          );

        /*
         * Descripción del día.
         */
        if (day.description) {

          doc.setFont(
            'helvetica',
            'italic'
          );

          doc.setFontSize(9);

          doc.setTextColor(
            ...this.theme.gray
          );

          const descriptionLines =
            doc.splitTextToSize(
              day.description,
              170
            );

          doc.text(
            descriptionLines,
            20,
            currentY
          );

          currentY +=
            descriptionLines.length * 4 +
            5;
        }

        /*
         * Ejercicios.
         */
        day.exercises.forEach(
          (exercise) => {

            /*
             * Estimamos el espacio mínimo
             * que necesita un ejercicio.
             */
            const estimatedHeight =
              55;

            if (
              currentY +
              estimatedHeight >
              265
            ) {

              doc.addPage();

              currentY = 18;
            }

            currentY =
              this.addExercise(
                doc,
                exercise,
                currentY
              );

            currentY += 7;
          }
        );

        /*
         * Separación entre días.
         */
        currentY += 5;
      }
    );

    /*
     * Calentamiento.
     */
    if (routine.warmup) {

      if (
        currentY + 45 >
        265
      ) {

        doc.addPage();

        currentY = 18;
      }

      currentY =
        this.addSimpleSection(
          doc,
          'CALENTAMIENTO',
          routine.warmup.durationMinutes,
          routine.warmup.activities,
          currentY
        );
    }

    /*
     * Enfriamiento.
     */
    if (routine.cooldown) {

      if (
        currentY + 45 >
        265
      ) {

        doc.addPage();

        currentY = 18;
      }

      currentY =
        this.addSimpleSection(
          doc,
          'ENFRIAMIENTO',
          routine.cooldown.durationMinutes,
          routine.cooldown.activities,
          currentY
        );
    }

    /*
     * Notas generales.
     */
    if (
      routine.generalNotes &&
      routine.generalNotes.length > 0
    ) {

      if (
        currentY + 45 >
        265
      ) {

        doc.addPage();

        currentY = 18;
      }

      currentY =
        this.addNotes(
          doc,
          routine.generalNotes,
          currentY
        );
    }

    return currentY;
  }


  // ============================================================
  // HEADER DEL DÍA
  // ============================================================

  private addDayHeader(
    doc: jsPDF,
    day: RoutineDay,
    y: number
  ): number {

    /*
     * Barra negra.
     */
    doc.setFillColor(
      ...this.theme.dark
    );

    doc.setDrawColor(
      ...this.theme.gold
    );

    doc.setLineWidth(0.8);

    doc.roundedRect(
      20,
      y,
      170,
      14,
      1.5,
      1.5,
      'FD'
    );

    /*
     * Barra roja lateral.
     */
    doc.setFillColor(
      ...this.theme.primary
    );

    doc.roundedRect(
      20,
      y,
      6,
      14,
      1.5,
      1.5,
      'F'
    );

    /*
     * Número del día.
     */
    doc.setFont(
      'helvetica',
      'bold'
    );

    doc.setFontSize(12.5);

    doc.setTextColor(
      ...this.theme.light
    );

    const title =
      `DÍA ${day.dayNumber}: ${day.name}`;

    doc.text(
      title,
      31,
      y + 8.8
    );

    return y + 19;
  }


  // ============================================================
  // EJERCICIO
  // ============================================================

  private addExercise(
    doc: jsPDF,
    exercise: Exercise,
    y: number
  ): number {

    /*
     * Título del ejercicio.
     *
     * Más pequeño que el título del día
     * para generar jerarquía visual.
     */
    doc.setFillColor(
      42,
      42,
      42
    );

    doc.setDrawColor(
      ...this.theme.gold
    );

    doc.setLineWidth(0.6);

    doc.roundedRect(
      25,
      y,
      160,
      10,
      1,
      1,
      'FD'
    );

    /*
     * Barra roja.
     */
    doc.setFillColor(
      ...this.theme.primary
    );

    doc.rect(
      25,
      y,
      5,
      10,
      'F'
    );

    /*
     * Nombre.
     */
    doc.setFont(
      'helvetica',
      'bold'
    );

    doc.setFontSize(10.5);

    doc.setTextColor(
      ...this.theme.light
    );

    doc.text(
      exercise.name,
      35,
      y + 6.5
    );

    y += 13;

    /*
     * Datos de series.
     */
    const series =
      exercise.seriesRepetitions || [];

    const repetitions =
      series.map(
        s => s.repetitions
      );

    /*
     * Tabla.
     *
     * Serie ocupa 35%.
     * Repeticiones ocupa 65%.
     */
    autoTable(
      doc,
      {

        startY: y,

        margin: {
          left: 25,
          right: 25,
          top: 10,
          bottom: 28
        },

        head: [
          [
            'SERIE',
            'REPETICIONES'
          ]
        ],

        body: repetitions.map(
          (rep, index) => [

            String(index + 1)
              .padStart(2, '0'),

            rep
          ]
        ),

        theme: 'grid',

        tableWidth: 160,

        columnStyles: {

          0: {
            cellWidth: 55,
            halign: 'center'
          },

          1: {
            cellWidth: 105,
            halign: 'center'
          }
        },

        headStyles: {

          fillColor:
            this.theme.primary,

          textColor:
            this.theme.light,

          fontStyle:
            'bold',

          fontSize: 8,

          halign:
            'center',

          valign:
            'middle',

          cellPadding: 2.5
        },

        bodyStyles: {

          fillColor:
            [248, 248, 248],

          textColor:
            [25, 25, 25],

          fontStyle:
            'bold',

          fontSize: 8.5,

          halign:
            'center',

          valign:
            'middle',

          cellPadding: 2.5
        },

        alternateRowStyles: {

          fillColor:
            [238, 238, 238]
        },

        styles: {

          lineColor:
            this.theme.gold,

          lineWidth:
            0.25,

          cellPadding:
            2.5,

          overflow:
            'linebreak',

          valign:
            'middle'
        }
      }
    );

    /*
     * Obtenemos la posición final de la tabla.
     */
    const tableEnd =
      (doc as any).lastAutoTable.finalY;

    y = tableEnd + 5;

    /*
     * Tarjetas de peso y descanso.
     */
    const cardWidth = 77.5;

    const gap = 5;

    /*
     * PESO.
     */
    this.addInfoCard(
      doc,
      25,
      y,
      cardWidth,
      'PESO SUGERIDO',
      exercise.suggestedWeight || '-'
    );

    /*
     * DESCANSO.
     */
    this.addInfoCard(
      doc,
      25 +
      cardWidth +
      gap,
      y,
      cardWidth,
      'DESCANSO',
      exercise.restSeconds
        ? `${exercise.restSeconds} segundos`
        : '-'
    );

    return y + 20;
  }


  // ============================================================
  // TARJETA INFORMACIÓN
  // ============================================================

  private addInfoCard(
    doc: jsPDF,
    x: number,
    y: number,
    width: number,
    label: string,
    value: string
  ): void {

    doc.setFillColor(
      42,
      42,
      42
    );

    doc.setDrawColor(
      ...this.theme.gold
    );

    doc.setLineWidth(0.6);

    doc.roundedRect(
      x,
      y,
      width,
      17,
      1.5,
      1.5,
      'FD'
    );

    /*
     * Label.
     */
    doc.setFont(
      'helvetica',
      'bold'
    );

    doc.setFontSize(6.5);

    doc.setTextColor(
      ...this.theme.gold
    );

    doc.text(
      label,
      x + 5,
      y + 6
    );

    /*
     * Valor.
     */
    doc.setFont(
      'helvetica',
      'bold'
    );

    doc.setFontSize(9);

    doc.setTextColor(
      ...this.theme.light
    );

    doc.text(
      String(value),
      x + 5,
      y + 12.5
    );
  }


  // ============================================================
  // CALENTAMIENTO / ENFRIAMIENTO
  // ============================================================

  private addSimpleSection(
    doc: jsPDF,
    title: string,
    duration: number,
    activities: string[],
    y: number
  ): number {

    /*
     * Título.
     */
    doc.setFillColor(
      ...this.theme.dark
    );

    doc.setDrawColor(
      ...this.theme.gold
    );

    doc.roundedRect(
      20,
      y,
      170,
      12,
      1,
      1,
      'FD'
    );

    doc.setFillColor(
      ...this.theme.primary
    );

    doc.rect(
      20,
      y,
      5,
      12,
      'F'
    );

    doc.setFont(
      'helvetica',
      'bold'
    );

    doc.setFontSize(10);

    doc.setTextColor(
      ...this.theme.light
    );

    doc.text(
      `${title} • ${duration} MIN`,
      31,
      y + 7.5
    );

    y += 16;

    /*
     * Actividades.
     */
    doc.setFont(
      'helvetica',
      'normal'
    );

    doc.setFontSize(8.5);

    doc.setTextColor(
      ...this.theme.dark
    );

    activities.forEach(
      activity => {

        doc.text(
          `• ${activity}`,
          25,
          y
        );

        y += 5;
      }
    );

    return y + 4;
  }


  // ============================================================
  // NOTAS
  // ============================================================

  private addNotes(
    doc: jsPDF,
    notes: string[],
    y: number
  ): number {

    doc.setFillColor(
      ...this.theme.dark
    );

    doc.setDrawColor(
      ...this.theme.gold
    );

    doc.roundedRect(
      20,
      y,
      170,
      12,
      1,
      1,
      'FD'
    );

    doc.setFillColor(
      ...this.theme.primary
    );

    doc.rect(
      20,
      y,
      5,
      12,
      'F'
    );

    doc.setFont(
      'helvetica',
      'bold'
    );

    doc.setFontSize(10);

    doc.setTextColor(
      ...this.theme.light
    );

    doc.text(
      'NOTAS GENERALES',
      31,
      y + 7.5
    );

    y += 17;

    doc.setFont(
      'helvetica',
      'normal'
    );

    doc.setFontSize(8.5);

    doc.setTextColor(
      ...this.theme.dark
    );

    notes.forEach(
      note => {

        const lines =
          doc.splitTextToSize(
            `• ${note}`,
            165
          );

        doc.text(
          lines,
          25,
          y
        );

        y +=
          lines.length * 4.5 +
          2;
      }
    );

    return y + 3;
  }


  // ============================================================
  // FOOTER
  // ============================================================

  private addFooters(
    doc: jsPDF
  ): void {

    const pageCount =
      doc.getNumberOfPages();

    for (
      let page = 1;
      page <= pageCount;
      page++
    ) {

      doc.setPage(page);

      /*
       * Línea superior del footer.
       */
      doc.setDrawColor(
        ...this.theme.gold
      );

      doc.setLineWidth(0.5);

      doc.line(
        20,
        278,
        190,
        278
      );

      /*
       * Nombre.
       */
      doc.setFont(
        'helvetica',
        'bold'
      );

      doc.setFontSize(6.5);

      doc.setTextColor(
        ...this.theme.primary
      );

      doc.text(
        'CENTRO DEPORTIVO',
        20,
        284
      );

      /*
       * Frase central.
       */
      doc.setFont(
        'helvetica',
        'normal'
      );

      doc.setTextColor(
        ...this.theme.gray
      );

      doc.text(
        'DISCIPLINA • FUERZA • DETERMINACIÓN',
        105,
        284,
        {
          align: 'center'
        }
      );

      /*
       * Página.
       */
      doc.text(
        `PÁGINA ${page} DE ${pageCount}`,
        190,
        284,
        {
          align: 'right'
        }
      );
    }
  }


  // ============================================================
  // UTILIDADES
  // ============================================================

  private getBadgeWidth(
    doc: jsPDF,
    text: string
  ): number {

    doc.setFont(
      'helvetica',
      'bold'
    );

    doc.setFontSize(7.8);

    const textWidth =
      doc.getTextWidth(text);

    return Math.max(
      textWidth + 14,
      30
    );
  }


  private sanitizeFileName(
    name: string
  ): string {

    return name
      .normalize('NFD')
      .replace(
        /[\u0300-\u036f]/g,
        ''
      )
      .replace(
        /[^a-zA-Z0-9-_ ]/g,
        ''
      )
      .replace(
        /\s+/g,
        '_'
      )
      .trim();
  }
}