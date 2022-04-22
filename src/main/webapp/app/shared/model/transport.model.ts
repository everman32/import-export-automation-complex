export interface ITransport {
  id?: number;
  brand?: string;
  model?: string;
  vin?: string;
}

export const defaultValue: Readonly<ITransport> = {};
