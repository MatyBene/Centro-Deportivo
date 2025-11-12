import { Component, effect, signal, Signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ChatMessageDisplay } from '../../models/Chat';
import { ChatService } from '../../services/chat-service';

@Component({
  selector: 'app-chat-widget',
  imports: [DatePipe],
  templateUrl: './chat-widget.html',
  styleUrl: './chat-widget.css'
})
export class ChatWidget {
  isOpen = signal(false);
  messages: Signal<ChatMessageDisplay[]>;
  newMessage = signal('');
  isLoading = signal(false);

  constructor(private chatService: ChatService) {
    this.messages = this.chatService.messages$;
    
    effect(() => {
      const currentMessages = this.messages();
      if (currentMessages.length > 0) {
        setTimeout(() => this.scrollToBottom(), 100);
      }
    });
  }

  toggleChat(): void {
    this.isOpen.update(value => !value);
    if (this.isOpen()) {
      setTimeout(() => this.scrollToBottom(), 100);
    }
  }

  sendMessage(): void {
    const message = this.newMessage().trim();
    
    if (message && !this.isLoading()) {
      this.isLoading.set(true);
      this.newMessage.set('');

      this.chatService.sendMessage(message).subscribe({
        next: () => {
          this.isLoading.set(false);
          this.scrollToBottom();
        },
        error: (error) => {
          console.error('Error al enviar mensaje:', error);
          this.isLoading.set(false);
          
          const errorMessage: ChatMessageDisplay = {
            text: 'Lo siento, hubo un error al procesar tu mensaje. Por favor intenta de nuevo.',
            isUser: false,
            timestamp: new Date()
          };
          
          // Usamos el servicio para agregar el mensaje de error
          this.chatService.getMessages();
        }
      });
    }
  }

  clearHistory(): void {
    if (confirm('¿Estás seguro de que quieres borrar todo el historial?')) {
      this.chatService.clearHistory();
    }
  }

  updateMessage(value: string): void {
    this.newMessage.set(value);
  }

  private scrollToBottom(): void {
    const chatMessages = document.querySelector('.chat-messages');
    if (chatMessages) {
      chatMessages.scrollTop = chatMessages.scrollHeight;
    }
  }

  onKeyPress(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.sendMessage();
    }
  }
}
