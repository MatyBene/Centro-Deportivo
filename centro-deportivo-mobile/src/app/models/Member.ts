import EnrolledActivitySummary from "./EnrolledActivitySummary"

export interface Member {
    id: number,
    name: string,
    lastname: string,
    dni: string,
    birthdate: string,
    phone: string,
    email: string,
    username: string,
    password: string,
    status: string,
    role: string,
    enrollments?: EnrolledActivitySummary[]
}