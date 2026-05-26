import { Prop, Schema, SchemaFactory } from '@nestjs/mongoose';
import { HydratedDocument } from 'mongoose';

export type AnxietyEventDocument = HydratedDocument<AnxietyEvent>;

@Schema()
export class AnxietyEvent {
  @Prop({
    required: true,
  })
  user!: string;

  @Prop({
    required: true,
    default: 0,
  })
  clicksCount!: number;

  @Prop({
    required: true,
  })
  date!: string;
}

export const AnxietyEventSchema = SchemaFactory.createForClass(AnxietyEvent);
