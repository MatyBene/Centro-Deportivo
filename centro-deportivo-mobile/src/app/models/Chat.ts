export interface ChatMessage {
  message: string;
  userId?: string;
}

export interface ChatResponse {
  response: string;
  timestamp: string;
  success: boolean;
}

export interface ChatMessageDisplay {
  text: string;
  isUser: boolean;
  timestamp: Date;
}