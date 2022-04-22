export interface IProduct {
  id?: number;
  name?: string;
  number?: number;
  cost?: number;
}

export const defaultValue: Readonly<IProduct> = {};
