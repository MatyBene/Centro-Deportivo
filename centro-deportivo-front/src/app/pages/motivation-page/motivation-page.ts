import { CommonModule } from '@angular/common';
import { Component, HostListener, OnDestroy, OnInit } from '@angular/core';

@Component({
  selector: 'app-motivation-page',
  imports: [CommonModule],
  templateUrl: './motivation-page.html',
  styleUrl: './motivation-page.css'
})
export class MotivationPage implements OnInit, OnDestroy{
  slideImages = [
        'assets/img/derek.png',
        'assets/img/keone.png',
        'assets/img/ramon.png',
        'assets/img/ryan.png',
    ];
    currentSlideIndex: number = 0;
    slideInterval: any;
    
    startSlideShow() {
        this.slideInterval = setInterval(() => {
            this.currentSlideIndex = (this.currentSlideIndex + 1) % this.slideImages.length;
        }, 3000); 
    }

motivations = [
        { text: "No existe el 'no puedo', solo 'no quiero'. Si quieres, harás que suceda.", 
          direction: 'left', visible: false, author: "Arnold Schwarzenegger", 
          authorImage: "assets/img/arnold1.jpg" }, 
        
        { text: "Todo el mundo quiere ser una bestia, pero nadie quiere hacer lo que hacen las bestias.", 
          direction: 'right', visible: false, author: "Ronnie Coleman", 
          authorImage: "assets/img/ronnie1.jpg" }, 
        
        { text: "Si no estás dispuesto a arriesgarlo todo, entonces no estás dispuesto a ser grande.", 
          direction: 'left', visible: false, author: "Dorian Yates", 
          authorImage: "assets/img/dorian1.jpg" }, 
        
        { text: "El físico de un campeón se construye sobre la base de la fe en ti mismo.", 
          direction: 'right', visible: false, author: "Lee Haney", 
          authorImage: "assets/img/leehaney1.jpg" }, 
        
        { text: "Cada día es una oportunidad para transformar tu debilidad en tu fuerza más grande.", 
          direction: 'left', visible: false, author: "Lou Ferrigno", 
          authorImage: "assets/img/lowferrigno1.jpg" }, 
        
        { text: "La única manera de fallar es no intentarlo. Si lo visualizas, puedes vivirlo.", 
          direction: 'right', visible: false, author: "Kai Greene", 
          authorImage: "assets/img/kaigreene1.jpg" }, 
        
        { text: "La mente es el límite. Mientras la mente pueda imaginar que puedes hacer algo, lo puedes hacer.", 
          direction: 'left', visible: false, author: "Tom Platz", 
          authorImage: "assets/img/tomplatz1.jpg" }, 
        
        { text: "La ambición es la chispa. La acción es el combustible.", 
          direction: 'right', visible: false, author: "Jay Cutler", 
          authorImage: "assets/img/jaycutler1.jpg" }, 
        
        { text: "Deja que tu cuerpo sea la prueba viviente de tu dedicación inquebrantable.", 
          direction: 'left', visible: false, author: "Arnold Schwarzenegger", 
          authorImage: "assets/img/arnold2.jpg" } 
    ];


  ngOnInit(): void {
    this.startSlideShow();
    this.checkScroll();
  }

  ngOnDestroy(): void {
    clearInterval(this.slideInterval);
  }

 @HostListener('window:scroll', [])
    checkScroll() {
        const scrollRevealThreshold = window.scrollY + window.innerHeight * 0.45; 
        
        
        const phraseVerticalSpacing = 200; 
        const initialOffset = 700; 
        
        this.motivations.forEach((m, index) => {
            const phraseApproximatePosition = (index * phraseVerticalSpacing) + initialOffset; 

            if (scrollRevealThreshold > phraseApproximatePosition && !m.visible) {
                m.visible = true;
            }
        });
    }
}
