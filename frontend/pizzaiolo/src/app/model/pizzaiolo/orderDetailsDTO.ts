/**
 * Api Documentation
 * Api Documentation
 *
 * OpenAPI spec version: 1.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
import { CestaEditDTO } from './cestaEditDTO';

export interface OrderDetailsDTO { 
    address?: string;
    amount?: number;
    comment?: string;
    deliveryDate?: Date;
    idChef?: string;
    idCourier?: string;
    idOrder?: number;
    idUser?: string;
    orderDate?: Date;
    orderStatus?: string;
    pizzas?: Array<CestaEditDTO>;
}