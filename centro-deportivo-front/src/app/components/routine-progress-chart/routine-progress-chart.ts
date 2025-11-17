import { Component, ViewChild, ElementRef, AfterViewInit, viewChild, input, signal, effect } from '@angular/core';
import { Chart, ChartConfiguration, registerables } from 'chart.js';
import { TrainingHistory } from '../../models/Routine';

Chart.register(...registerables);

@Component({
  selector: 'app-routine-progress-chart',
  imports: [],
  templateUrl: './routine-progress-chart.html',
  styleUrl: './routine-progress-chart.css'
})
export class RoutineProgressChart implements AfterViewInit{
  chartCanvas = viewChild<ElementRef<HTMLCanvasElement>>('chartCanvas');
  exerciseId = input.required<string>();
  exerciseName = input.required<string>();
  history = input<TrainingHistory[]>([]);

  loading = signal(false);
  hasData = signal(false);
  totalWorkouts = signal(0);  

  private chart?: Chart;

  constructor() {
    effect(() => {
      const hist = this.history();
      if(hist && hist.length > 0) {
        setTimeout(() => this.processData(), 100);
      }
    })
  }

  ngAfterViewInit(): void {
    setTimeout(() => this.processData(), 200);
  }

  processData() {
    const hist = this.history();

    if(!hist || hist.length === 0) {
      this.hasData.set(false);
      console.log('No hay datos históricos');
      return;
    }

    this.hasData.set(true);
    this.totalWorkouts.set(hist.length);
    // hist.forEach(th => {
    //   th.sets.forEach(set => {

    //   })
    // })

    this.createChart();
  }

  createChart() {
    const canvasRef = this.chartCanvas();
    
    if (!canvasRef) {
      console.error('Canvas ref not found!');
      return;
    }

    if (this.chart) {
      this.chart.destroy();
    }

    const setsMap = new Map<number, { date: string, weight: number }[]>();

    const hist = this.history();
    hist.forEach(th => {
      th.sets.forEach(set => {
        if(!setsMap.has(set.number)) {
          setsMap.set(set.number, [])
        }
        setsMap.get(set.number)!.push({
          date: th.date,
          weight: set.weight
        })
      })
    })

    const colors = [
      { bg: 'rgba(209, 44, 44, 0.3)', border: 'rgb(209, 44, 44)' },
      { bg: 'rgba(201, 168, 62, 0.3)', border: 'rgb(201, 168, 62)' },
      { bg: 'rgba(255, 107, 107, 0.3)', border: 'rgb(255, 107, 107)' },
      { bg: 'rgba(255, 193, 7, 0.3)', border: 'rgb(255, 193, 7)' },
      { bg: 'rgba(139, 0, 0, 0.3)', border: 'rgb(139, 0, 0)' },
    ]

    const datasets = Array.from(setsMap.entries()).map(([setNumber, data], index) => {
      const color = colors[index % colors.length];
      return {
        label: `Serie ${setNumber}`,
        data: data.map(d => d.weight),
        backgroundColor: color.bg,
        borderColor: color.border,
        borderWidth: 2,
        tension: 0.4,
        fill: true
      }
    })

    const uniqueDates = [...new Set(hist.map(h => h.date))].sort();

    const config: ChartConfiguration = {
      type: 'line',
      data: {
        labels: uniqueDates.map(d => this.formatDate(d)),
        datasets: datasets
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'top',
            labels: {
              color: '#c9a83e',
              font: {
                family: 'MiSans, sans-serif',
                size: 14,
                weight: 'bold'
              },
              padding: 15,
              boxWidth: 40,
              boxHeight: 3
            }
          },
          title: {
            display: true,
            text: 'Evolución del Peso por Serie (kg)',
            color: '#c9a83e',
            font: {
              family: 'MiSans, sans-serif',
              size: 18,
              weight: 'bold'
            },
            padding: {
              top: 10,
              bottom: 20
            }
          },
          tooltip: {
            backgroundColor: 'rgba(0, 0, 0, 0.9)',
            titleColor: '#c9a83e',
            bodyColor: '#fff',
            borderColor: '#c9a83e',
            borderWidth: 2,
            padding: 12,
            displayColors: true,
            titleFont: {
              family: 'MiSans, sans-serif',
              size: 14,
              weight: 'bold'
            },
            bodyFont: {
              family: 'MiSans, sans-serif',
              size: 13
            },
            callbacks: {
              label: function(context) {
                return `${context.dataset.label}: ${context.parsed.y} kg`;
              }
            }
          }
        },
        scales: {
          y: {
            beginAtZero: false,
            grid: {
              color: 'rgba(201, 168, 62, 0.2)',
              lineWidth: 1
            },
            ticks: {
              color: '#c9a83e',
              font: {
                family: 'MiSans, sans-serif',
                size: 12,
                weight: 'bold'
              },
              padding: 8
            },
            title: {
              display: true,
              text: 'Peso (kg)',
              color: '#c9a83e',
              font: {
                family: 'MiSans, sans-serif',
                size: 14,
                weight: 'bold'
              }
            }
          },
          x: {
            grid: {
              color: 'rgba(201, 168, 62, 0.1)',
              lineWidth: 1
            },
            ticks: {
              color: '#c9a83e',
              font: {
                family: 'MiSans, sans-serif',
                size: 12,
                weight: 'bold'
              },
              padding: 8
            },
            title: {
              display: true,
              text: 'Fecha',
              color: '#c9a83e',
              font: {
                family: 'MiSans, sans-serif',
                size: 14,
                weight: 'bold'
              }
            }
          }
        }
      }
    }

    try {
      this.chart = new Chart(canvasRef.nativeElement, config);
    } catch (error) {
      console.error('Error creating chart:', error);
    }
  }

  formatDate(date: string) {
    const [year, month, day] = date.split('-');
    return`${day}/${month}`;
  }

  close() {
    if (this.chart) {
      this.chart.destroy();
    }
    // Emitir evento de cierre (se implementará en el componente padre)
  }

}
