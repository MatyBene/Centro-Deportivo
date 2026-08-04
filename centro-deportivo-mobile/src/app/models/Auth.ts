export interface LoginRequest{
    username: string,
    password: string
}

export interface LoginResponse{
    token: string
}

export interface TokenPayLoad {
    sub: string,
    rol: Array<{authority: string}>
}