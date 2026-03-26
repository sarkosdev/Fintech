export interface ITransaction {
    id: number;
    balance: number;
    timestamp: string;
    senderId: number;
    receiverId: number;
    operationType: string;
    counterpartyEmail: string;
}