import SportActivitySummary from "./SportActivitySummary";

export default interface Instructor {
    id: number,
    name: string,
    lastname: string,
    specialty: string,
    birthdate: string,
    activities: SportActivitySummary[],
    role: string,
    username: string,
    password: string
}