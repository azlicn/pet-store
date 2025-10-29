import { Pet } from "./pet.model";

export interface OrderItem {
    id?: number;
    price: number;
    pet: Pet;

}