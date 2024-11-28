import { IProductUnit } from 'app/shared/model/product-unit.model';

export interface IProduct {
  id?: number;
  name?: string;
  costPerPiece?: number;
  productUnit?: IProductUnit | null;
}

export const defaultValue: Readonly<IProduct> = {};
