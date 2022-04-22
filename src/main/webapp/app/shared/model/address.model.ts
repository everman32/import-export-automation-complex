export interface IAddress {
  id?: number;
  country?: string;
  city?: string;
  postcode?: string;
}

export const defaultValue: Readonly<IAddress> = {};
