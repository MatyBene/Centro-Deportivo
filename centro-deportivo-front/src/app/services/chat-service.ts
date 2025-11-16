import { Injectable, signal, computed } from '@angular/core';
import { environment } from '../../environments/environment';
import { ChatMessage, ChatMessageDisplay, ChatResponse } from '../models/Chat';
import { HttpClient } from '@angular/common/http';
import { map, Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = `${environment.apiUrl}/chat`;

  private messagesSignal = signal<ChatMessageDisplay[]>(this.loadMessages());

  public messages$ = computed(() => this.messagesSignal());

  public messageCount = computed(() => this.messagesSignal().length);

  constructor(private http: HttpClient) {}

  private loadMessages(): ChatMessageDisplay[] {
    const stored = localStorage.getItem('chatMessages');
    return stored ? JSON.parse(stored) : [];
  }

  private saveMessages(messages: ChatMessageDisplay[]): void {
    localStorage.setItem('chatMessages', JSON.stringify(messages));
  }

  sendMessage(message: string): Observable<ChatMessageDisplay> {
    const userMessage: ChatMessageDisplay = {
      text: message,
      isUser: true,
      timestamp: new Date()
    };

    this.messagesSignal.update(messages => [...messages, userMessage]);
    this.saveMessages(this.messagesSignal());

    const chatMessage: ChatMessage = { message };

    return this.http.post<ChatResponse>(`${this.apiUrl}/message`, chatMessage).pipe(
      map(response => ({
        text: response.response,
        isUser: false,
        timestamp: new Date(response.timestamp)
      })),
      tap(botMessage => {
        this.messagesSignal.update(messages => [...messages, botMessage]);
        this.saveMessages(this.messagesSignal());
      })
    );
  }

  clearHistory(): void {
    this.messagesSignal.set([]);
    localStorage.removeItem('chatMessages');
  }

  getMessages(): ChatMessageDisplay[] {
    return this.messagesSignal();
  }

}
