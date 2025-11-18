export default interface SportActivity {
    name: string,
    maxMembers: number,
    instructorId: number,
    instructorName: string,
    description: string,
    currentMembers: number,
    startTime: string,
    endTime: string,
    classDays: string[];
}